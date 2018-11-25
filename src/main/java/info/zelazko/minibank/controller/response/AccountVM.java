package info.zelazko.minibank.controller.response;

import info.zelazko.minibank.persistance.model.Account;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.Optional;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountVM extends ViewModel {
    private String iban;
    private int balance;
    private String currency;

    @Builder(toBuilder = true)
    public AccountVM(String iban, int balance, String currency, ErrorResponse error) {
        super(error);
        this.iban = iban;
        this.balance = balance;
        this.currency = currency;
    }

    public AccountVM(Account account) {
        super(null);
        this.iban = account.getIban();
        this.balance = account.getBalance();
        this.currency = Optional.ofNullable(account.getCurrency()).map(c -> c.getCurrencyCode()).orElse(null);
    }
}
