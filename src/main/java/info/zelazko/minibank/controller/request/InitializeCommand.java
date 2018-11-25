package info.zelazko.minibank.controller.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InitializeCommand {
    private String source;
    private String destination;
    private int amount;
    private String currency;

}
