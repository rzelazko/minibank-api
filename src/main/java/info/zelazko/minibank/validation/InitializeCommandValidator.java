package info.zelazko.minibank.validation;

import info.zelazko.minibank.controller.request.InitializeCommand;
import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.MinibankDao;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static info.zelazko.minibank.util.ErrorMessages.ERROR_CODE_EMPTY_REQUEST;
import static info.zelazko.minibank.util.ErrorMessages.ERROR_MSG_EMPTY_REQUEST;

@Value
public class InitializeCommandValidator implements Validable {
    private final InitializeCommand request;
    private final MinibankDao dao;

    @Override
    public void validate() {
        Optional.ofNullable(request).orElseThrow(() -> new ValidationException(ERROR_MSG_EMPTY_REQUEST, ERROR_CODE_EMPTY_REQUEST));

        List<Validable> validators = Arrays.asList(
                new IbanValidator(request.getSource(), false, dao),
                new IbanValidator(request.getDestination(), false, dao),
                new CurrencyCodeValidator(request.getCurrency()),
                new TransferAmountValidator(request.getAmount()),
                new TransferSourceAccountValidator(request.getSource(), request.getAmount(), request.getCurrency(), dao),
                new TransferDestinationAccountValidator(request.getDestination(), request.getCurrency(), dao)
        );

        validators.stream().forEach(Validable::validate);
    }

}
