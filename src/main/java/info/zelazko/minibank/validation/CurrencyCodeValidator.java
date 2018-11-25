package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.util.ErrorMessages;
import lombok.Value;
import spark.utils.StringUtils;

@Value
public class CurrencyCodeValidator implements Validable {
    private static final int CURRENCY_CODE_LENGTH = 3;

    private final String currencyCode;

    @Override
    public void validate() {
        if (StringUtils.isEmpty(currencyCode) || currencyCode.length() != CURRENCY_CODE_LENGTH) {
            throw new ValidationException(
                    String.format(ErrorMessages.ERROR_MSG_INVALID_CURRENCY, currencyCode),
                    ErrorMessages.ERROR_CODE_INVALID_CURRENCY);
        }
    }
}
