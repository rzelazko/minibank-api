package info.zelazko.minibank.validation;

import info.zelazko.minibank.controller.request.InitializeCommand;
import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.util.MinibankError;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Value
public class InitializeCommandValidator implements Validable {
    private final InitializeCommand request;
    private final MinibankDao dao;

    @Override
    public void validate() {
        Optional.ofNullable(request).orElseThrow(() -> new ValidationException(MinibankError.EMPTY_REQUEST));

        List<Validable> validators = Arrays.asList(
                new IbanValidator(request.getSource()),
                new IbanValidator(request.getDestination()),
                new CurrencyCodeValidator(request.getCurrency()),
                new TransferAmountValidator(request.getAmount()),
                new TransferSourceAccountValidator(request.getSource(), request.getAmount(), request.getCurrency(), dao),
                new TransferDestinationAccountValidator(request.getDestination(), request.getCurrency(), dao)
        );

        validators.stream().forEach(Validable::validate);
    }

}
