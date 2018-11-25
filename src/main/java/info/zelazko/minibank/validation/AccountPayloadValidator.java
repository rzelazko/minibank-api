package info.zelazko.minibank.validation;

import info.zelazko.minibank.controller.request.AccountPayload;
import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.MinibankDao;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static info.zelazko.minibank.util.ErrorMessages.ERROR_CODE_EMPTY_REQUEST;
import static info.zelazko.minibank.util.ErrorMessages.ERROR_MSG_EMPTY_REQUEST;

@Value
public class AccountPayloadValidator implements Validable {
    private final AccountPayload accountPayload;
    private final MinibankDao dao;

    public void validate() {
        Optional.ofNullable(accountPayload).orElseThrow(() -> new ValidationException(ERROR_MSG_EMPTY_REQUEST, ERROR_CODE_EMPTY_REQUEST));

        List<Validable> validators = Arrays.asList(
                new IbanValidator(accountPayload.getIban(), true, dao),
                new CurrencyCodeValidator(accountPayload.getCurrency())
        );

        validators.stream().forEach(Validable::validate);
    }
}
