package info.zelazko.minibank.exception.validation;

import info.zelazko.minibank.util.MinibankError;
import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final String code;

    public ValidationException(String message, String code) {
        super(message);
        this.code = code;
    }

    public ValidationException(MinibankError error) {
        super(error.getMessage());
        code = error.getCode();
    }

    public ValidationException(MinibankError error, Object... args) {
        this(String.format(error.getMessage(), args), error.getCode());
    }
}
