package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ValidationException;
import lombok.Value;

import static info.zelazko.minibank.util.ErrorMessages.ERROR_CODE_INVALID_AMOUNT;
import static info.zelazko.minibank.util.ErrorMessages.ERROR_MSG_INVALID_AMOUNT;

@Value
public class TransferAmountValidator implements Validable {
    private static final int MIN_AMOUNT = 1;

    private final int amount;

    @Override
    public void validate() {
        if (amount < MIN_AMOUNT) {
            throw new ValidationException(ERROR_MSG_INVALID_AMOUNT, ERROR_CODE_INVALID_AMOUNT);
        }
    }
}
