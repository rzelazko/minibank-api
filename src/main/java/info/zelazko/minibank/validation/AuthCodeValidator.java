package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.util.MinibankError;
import lombok.Value;
import spark.utils.StringUtils;

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
            throw new ValidationException(MinibankError.INVALID_AUTH_CODE);
        }
    }
}
