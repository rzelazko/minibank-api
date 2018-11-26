package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ValidationException;
import lombok.Value;
import spark.utils.StringUtils;

import static info.zelazko.minibank.util.ErrorMessages.ERROR_CODE_INVALID_AUTH_CODE;
import static info.zelazko.minibank.util.ErrorMessages.ERROR_MSG_INVALID_AUTH_CODE;

@Value
public class AuthCodeValidator implements Validable {
    /**
     * This is fake project - so valid SMS authorization code is hardcoded.
     */
    private final static String VALID_AUTH_CODE = "123456";

    private final String authCode;

    @Override
    public void validate() {
        if (StringUtils.isEmpty(authCode) || !VALID_AUTH_CODE.equalsIgnoreCase(authCode)) {
            throw new ValidationException(ERROR_MSG_INVALID_AUTH_CODE, ERROR_CODE_INVALID_AUTH_CODE);
        }
    }
}
