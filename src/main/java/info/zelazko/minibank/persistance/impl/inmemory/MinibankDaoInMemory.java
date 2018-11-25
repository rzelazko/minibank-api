package info.zelazko.minibank.persistance.impl.inmemory;

import info.zelazko.minibank.exception.DaoException;
import info.zelazko.minibank.persistance.model.Account;
import info.zelazko.minibank.persistance.model.Transfer;
import info.zelazko.minibank.persistance.MinibankDao;
import spark.utils.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MinibankDaoInMemory implements MinibankDao {
    private static final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private static final Map<String, Transfer> transfers = new ConcurrentHashMap<>();

    @Override
    public Optional<Account> findAccountByIban(String iban) {
        if (StringUtils.isBlank(iban)) {
            throw new IllegalArgumentException();
        }
        return Optional.ofNullable(accounts.get(iban));
    }

    @Override
    public Account createAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException();
        }
        accounts.put(account.getIban(), account);

        return account;
    }

    @Override
    public Account updateAccountBalance(String iban, int newBalance) {
        if (StringUtils.isBlank(iban)) {
            throw new IllegalArgumentException();
        }
        Account originalAccount = Optional.ofNullable(accounts.get(iban)).orElseThrow(() -> new DaoException());
        Account newAccount = Account.builder()
                .currency(originalAccount.getCurrency())
                .iban(originalAccount.getIban())
                .balance(newBalance)
                .build();
        accounts.put(iban, newAccount);

        return newAccount;
    }

    @Override
    public Optional<Transfer> findTransferByUuid(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new IllegalArgumentException();
        }
        return Optional.ofNullable(transfers.get(uuid));
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {
        if (transfer == null) {
            throw new IllegalArgumentException();
        }
        transfers.put(transfer.getUuid(), transfer);

        return transfer;
    }

    @Override
    public Transfer confirmTransfer(Transfer transferRequest) {
        if (transferRequest == null) {
            throw new IllegalArgumentException();
        }

        Transfer transfer = transfers.get(transferRequest.getUuid());
        Account source = findAccountByIban(transferRequest.getSource()).orElseThrow(() -> new DaoException());
        Account destination = findAccountByIban(transferRequest.getDestination()).orElseThrow(() -> new DaoException());

        try {
            if (!transferRequest.getCurrency().equals(source.getCurrency()) || !transferRequest.getCurrency().equals(destination.getCurrency())) {
                throw new DaoException();
            }

            if (!Transfer.Status.INITIALIZED.equals(transferRequest.getStatus())) {
                throw new DaoException();
            }

            updateTransferStatus(transfer.getUuid(), Transfer.Status.IN_PROGRESS);
            updateAccountBalance(source.getIban(), Math.subtractExact(source.getBalance(), transferRequest.getAmount()));
            updateAccountBalance(destination.getIban(), Math.addExact(destination.getBalance(), transferRequest.getAmount()));
            updateTransferStatus(transfer.getUuid(), Transfer.Status.CONFIRMED);
        } catch (DaoException e) {
            updateTransferStatus(transfer.getUuid(), Transfer.Status.FAILED);
            updateAccountBalance(source.getIban(), source.getBalance());
            updateAccountBalance(destination.getIban(), destination.getBalance());
            throw e;
        }

        return transfer;
    }

    @Override
    public Transfer deleteTransfer(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new IllegalArgumentException();
        }

        Transfer transfer = findTransferByUuid(uuid).orElseThrow(() -> new DaoException());
        transfers.remove(transfer.getUuid());

        return transfer;
    }

    @Override
    public Transfer updateTransferStatus(String uuid, Transfer.Status newStatus) {
        if (StringUtils.isBlank(uuid)) {
            throw new IllegalArgumentException();
        }
        Transfer originalTransfer = Optional.ofNullable(transfers.get(uuid)).orElseThrow(() -> new DaoException());
        Transfer newTransfer = Transfer.builder()
                .uuid(uuid)
                .status(newStatus)
                .source(originalTransfer.getSource())
                .destination(originalTransfer.getDestination())
                .amount(originalTransfer.getAmount())
                .currency(originalTransfer.getCurrency())
                .build();
        transfers.put(uuid, newTransfer);

        return newTransfer;
    }
}
