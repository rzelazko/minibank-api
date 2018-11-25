package info.zelazko.minibank.controller.response;

import lombok.Value;

@Value
public class ErrorResponse {
    private final String message;
    private final String code;
}
