package info.zelazko.minibank.exception.validation;

import info.zelazko.minibank.util.MinibankError;

public class ResourceNotFoundException extends ValidationException {

    public ResourceNotFoundException(MinibankError error, Object... args) {
        super(error, args);
    }

    public ResourceNotFoundException(MinibankError error) {
        super(error);
    }
}
