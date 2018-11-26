package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.util.MinibankError;
import lombok.Value;

@Value
public class TransferAmountValidator implements Validable {
    private static final int MIN_AMOUNT = 1;

    private final int amount;

    @Override
    public void validate() {
        if (amount < MIN_AMOUNT) {
            throw new ValidationException(MinibankError.INVALID_AMOUNT);
        }
    }
}
