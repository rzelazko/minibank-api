package info.zelazko.minibank.persistance;

import info.zelazko.minibank.persistance.model.Account;
import info.zelazko.minibank.persistance.model.Transfer;

import java.util.Optional;

public interface MinibankDao {
    Optional<Transfer> findTransferByUuid(String uuid);

    Transfer createTransfer(Transfer transfer);

    Transfer confirmTransfer(Transfer transfer);

    Transfer deleteTransfer(String uuid);

    Transfer updateTransferStatus(String uuid, Transfer.Status newStatus);

    Optional<Account> findAccountByIban(String iban);

    Account createAccount(Account account);

    Account updateAccountBalance(String iban, int newBalance);
}
