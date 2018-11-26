package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.util.MinibankError;
import lombok.Value;

@Value
public class BalanceValidator implements Validable {
    private static final int MIN_BALANCE = 0;

    private final int currentBalance;
    private final int withdrawAmount;

    public void validate() {
        if (Math.subtractExact(currentBalance, withdrawAmount) < MIN_BALANCE) {
            throw new ValidationException(MinibankError.INVALID_BALANCE);
        }
    }
}
