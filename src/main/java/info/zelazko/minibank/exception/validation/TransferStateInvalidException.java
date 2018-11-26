package info.zelazko.minibank.exception.validation;

import info.zelazko.minibank.util.MinibankError;

public class TransferStateInvalidException extends ValidationException {
    public TransferStateInvalidException(MinibankError error) {
        super(error);
    }
}
