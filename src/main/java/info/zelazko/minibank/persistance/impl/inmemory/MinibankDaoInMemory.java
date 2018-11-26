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
        Optional.ofNullable(iban).orElseThrow(IllegalArgumentException::new);
        return Optional.ofNullable(accounts.get(iban));
    }

    @Override
    public Account createAccount(Account account) {
        Optional.ofNullable(account).orElseThrow(IllegalArgumentException::new);
        accounts.put(account.getIban(), account);

        return account;
    }

    @Override
    public Account updateAccountBalance(String iban, int newBalance) {
        Optional.ofNullable(iban).orElseThrow(IllegalArgumentException::new);
        Account originalAccount = Optional.ofNullable(accounts.get(iban)).orElseThrow(DaoException::new);
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
        Optional.ofNullable(uuid).orElseThrow(IllegalArgumentException::new);
        return Optional.ofNullable(transfers.get(uuid));
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {
        Optional.ofNullable(transfer).orElseThrow(IllegalArgumentException::new);
        transfers.put(transfer.getUuid(), transfer);

        return transfer;
    }

    @Override
    public Transfer confirmTransfer(Transfer transferRequest) {
        Optional.ofNullable(transferRequest).orElseThrow(IllegalArgumentException::new);

        Transfer transfer = transfers.get(transferRequest.getUuid());
        Account source = findAccountByIban(transferRequest.getSource()).orElseThrow(DaoException::new);
        Account destination = findAccountByIban(transferRequest.getDestination()).orElseThrow(DaoException::new);

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
        Optional.ofNullable(uuid).orElseThrow(IllegalArgumentException::new);

        Transfer transfer = findTransferByUuid(uuid).orElseThrow(DaoException::new);
        transfers.remove(transfer.getUuid());

        return transfer;
    }

    @Override
    public Transfer updateTransferStatus(String uuid, Transfer.Status newStatus) {
        Optional.ofNullable(uuid).orElseThrow(IllegalArgumentException::new);
        Transfer originalTransfer = Optional.ofNullable(transfers.get(uuid)).orElseThrow(DaoException::new);
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
