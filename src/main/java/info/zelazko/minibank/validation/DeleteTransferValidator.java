package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.model.Transfer;
import info.zelazko.minibank.util.MinibankError;
import lombok.Value;

@Value
public class DeleteTransferValidator implements Validable {
    private final Transfer transfer;

    @Override
    public void validate() {
        if (!Transfer.Status.INITIALIZED.equals(transfer.getStatus())) {
            throw new ValidationException(MinibankError.DEL_NONINITIALIZED);
        }
    }
}
