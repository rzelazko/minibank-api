package info.zelazko.minibank.controller.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountPayload {
    private String iban;
    private String currency;
    private int balance;
}
