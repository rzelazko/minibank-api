package info.zelazko.minibank.persistance.model;

import lombok.Builder;
import lombok.Value;

import java.util.Currency;

@Value
@Builder
public class Transfer {
    public enum Status {
        INITIALIZED, IN_PROGRESS, CONFIRMED, FAILED
    }

    private String uuid;
    private String source;
    private String destination;
    private int amount;
    private Currency currency;
    private Status status;
}
