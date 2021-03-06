package info.zelazko.minibank.validation;

import info.zelazko.minibank.controller.request.ConfirmCommand;
import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.persistance.model.Transfer;
import info.zelazko.minibank.util.MinibankError;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Value
public class ConfirmCommandValidator implements Validable {
    private final ConfirmCommand request;
    private final Transfer transfer;
    private final MinibankDao dao;

    @Override
    public void validate() {
        Optional.ofNullable(request).orElseThrow(() -> new ValidationException(MinibankError.EMPTY_REQUEST));

        List<Validable> validators = Arrays.asList(
                new AuthCodeValidator(request.getAuthCode()),
                new TransferStatusValidator(transfer.getStatus()),
                new IbanValidator(transfer.getSource()),
                new IbanValidator(transfer.getDestination()),
                new CurrencyCodeValidator(transfer.getCurrency().getCurrencyCode()),
                new TransferAmountValidator(transfer.getAmount()),
                new TransferSourceAccountValidator(transfer.getSource(), transfer.getAmount(), transfer.getCurrency().getCurrencyCode(), dao),
                new TransferDestinationAccountValidator(transfer.getDestination(), transfer.getCurrency().getCurrencyCode(), dao)
        );

        validators.stream().forEach(Validable::validate);
    }
}
