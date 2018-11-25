package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.MinibankDao;
import lombok.Value;
import spark.utils.StringUtils;

import static info.zelazko.minibank.util.ErrorMessages.*;

@Value
public class IbanValidator implements Validable {
    private static final int IBAN_MIN_LENGTH = 16;
    private static final int IBAN_MAX_LENGTH = 32;
    private static final String IBAN_PATTERN = "[a-zA-Z]{2}[a-zA-Z0-9]{14,30}";

    private final String iban;
    private final boolean checkUnique;
    private final MinibankDao dao;

    public void validate() {
        if (StringUtils.isEmpty(iban) || iban.length() < IBAN_MIN_LENGTH || iban.length() > IBAN_MAX_LENGTH || !iban.matches(IBAN_PATTERN)) {
            throw new ValidationException(String.format(ERROR_MSG_INVALID_IBAN, iban), ERROR_CODE_INVALID_IBAN);
        }

        if (checkUnique && dao.findAccountByIban(iban).isPresent()) {
            throw new ValidationException(String.format(ERROR_MSG_ACCOUNT_EXISTS, iban), ERROR_CODE_ACCOUNT_EXISTS);
        }
    }

}
