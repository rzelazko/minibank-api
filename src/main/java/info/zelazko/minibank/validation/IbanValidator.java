package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.util.MinibankError;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import spark.utils.StringUtils;

@Value
@RequiredArgsConstructor
public class IbanValidator implements Validable {
    private static final int IBAN_MIN_LENGTH = 16;
    private static final int IBAN_MAX_LENGTH = 32;
    private static final String IBAN_PATTERN = "[a-zA-Z]{2}[a-zA-Z0-9]{14,30}";

    private final String iban;
    private final boolean checkUnique;
    private final MinibankDao dao;

    public IbanValidator(String iban) {
        this.iban = iban;
        checkUnique = false;
        dao = null;
    }

    @Override
    public void validate() {
        if (StringUtils.isEmpty(iban) || iban.length() < IBAN_MIN_LENGTH || iban.length() > IBAN_MAX_LENGTH || !iban.matches(IBAN_PATTERN)) {
            throw new ValidationException(MinibankError.INVALID_IBAN, iban);
        }

        if (checkUnique && dao.findAccountByIban(iban).isPresent()) {
            throw new ValidationException(MinibankError.ACCOUNT_EXISTS, iban);
        }
    }

}
