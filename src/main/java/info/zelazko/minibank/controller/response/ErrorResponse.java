package info.zelazko.minibank.controller.response;

import info.zelazko.minibank.util.MinibankError;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class ErrorResponse {
    private final String message;
    private final String code;

    public ErrorResponse(MinibankError error) {
        message = error.getMessage();
        code = error.getCode();
    }
}
