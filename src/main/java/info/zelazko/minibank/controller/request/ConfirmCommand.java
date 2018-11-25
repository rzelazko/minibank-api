package info.zelazko.minibank.controller.request;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ConfirmCommand {
    private String authCode;
}
