package info.zelazko.minibank.exception.validation;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final String code;

    public ValidationException(String message, String code) {
        super(message);
        this.code = code;
    }
}
