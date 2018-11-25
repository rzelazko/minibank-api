package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.model.Transfer;
import lombok.Value;

import static info.zelazko.minibank.util.ErrorMessages.ERROR_CODE_DEL_NONINITIALIZED;
import static info.zelazko.minibank.util.ErrorMessages.ERROR_MSG_DEL_NONINITIALIZED;

@Value
public class DeleteTransferValidator implements Validable {
    private final Transfer transfer;

    @Override
    public void validate() {
        if (!Transfer.Status.INITIALIZED.equals(transfer.getStatus())) {
            throw new ValidationException(ERROR_MSG_DEL_NONINITIALIZED, ERROR_CODE_DEL_NONINITIALIZED);
        }
    }
}
