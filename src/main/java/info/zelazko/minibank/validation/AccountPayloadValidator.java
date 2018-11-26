package info.zelazko.minibank.validation;

import info.zelazko.minibank.controller.request.AccountPayload;
import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.util.MinibankError;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Value
public class AccountPayloadValidator implements Validable {
    private final AccountPayload accountPayload;
    private final MinibankDao dao;

    public void validate() {
        Optional.ofNullable(accountPayload).orElseThrow(() -> new ValidationException(MinibankError.EMPTY_REQUEST));

        List<Validable> validators = Arrays.asList(
                new IbanValidator(accountPayload.getIban(), true, dao),
                new CurrencyCodeValidator(accountPayload.getCurrency())
        );

        validators.stream().forEach(Validable::validate);
    }
}
