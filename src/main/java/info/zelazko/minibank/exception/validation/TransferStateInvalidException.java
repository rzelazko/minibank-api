package info.zelazko.minibank.exception.validation;

public class TransferStateInvalidException extends ValidationException {
    public TransferStateInvalidException(String message, String code) {
        super(message, code);
    }
}
