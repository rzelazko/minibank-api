package info.zelazko.minibank.exception.validation;

public class ResourceNotFoundException extends ValidationException {

    public ResourceNotFoundException(String message, String code) {
        super(message, code);
    }
}
