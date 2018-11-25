package info.zelazko.minibank.persistance.model;

import lombok.Builder;
import lombok.Value;

import java.util.Currency;

@Value
@Builder
public class Account {
    private String iban;
    private int balance;
    private Currency currency;
}
