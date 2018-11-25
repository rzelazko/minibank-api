package info.zelazko.minibank.service.helper;

import info.zelazko.minibank.persistance.model.Account;
import info.zelazko.minibank.persistance.model.Transfer;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class MockBuilder {
    public static Optional<Account> prepareEurAccount(String iban, int balance) {
        return Optional.of(Account.builder()
                .iban(iban)
                .currency(MockValue.CURRENCY_EUR)
                .balance(balance)
                .build());
    }

    public static Optional<Account> preparePlnAccount(String iban, int balance) {
        return Optional.of(Account.builder()
                .iban(iban)
                .currency(MockValue.CURRENCY_PLN)
                .balance(balance)
                .build());
    }

    public static Optional<Transfer> prepareEurTransfer(String source, String destination, int amount) {
        return Optional.of(Transfer.builder()
                .uuid(MockValue.UUID1)
                .status(Transfer.Status.INITIALIZED)
                .source(source)
                .destination(destination)
                .amount(amount)
                .currency(MockValue.CURRENCY_EUR)
                .build());
    }

    public static Optional<Transfer> prepareEurTransfer(String source, String destination, int amount, Transfer.Status status) {
        return Optional.of(Transfer.builder()
                .uuid(MockValue.UUID1)
                .status(status)
                .source(source)
                .destination(destination)
                .amount(amount)
                .currency(MockValue.CURRENCY_EUR)
                .build());
    }
}
