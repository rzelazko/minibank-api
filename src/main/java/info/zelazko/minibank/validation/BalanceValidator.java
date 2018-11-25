package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ValidationException;
import lombok.Value;

import static info.zelazko.minibank.util.ErrorMessages.ERROR_CODE_INVALID_BALANCE;
import static info.zelazko.minibank.util.ErrorMessages.ERROR_MSG_INVALID_BALANCE;

@Value
public class BalanceValidator implements Validable {
    private static final int MIN_BALANCE = 0;

    private final int currentBalance;
    private final int withdrawAmount;

    public void validate() {
        if (Math.subtractExact(currentBalance, withdrawAmount) < MIN_BALANCE) {
            throw new ValidationException(ERROR_MSG_INVALID_BALANCE, ERROR_CODE_INVALID_BALANCE);
        }
    }
}
