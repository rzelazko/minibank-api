package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.TransferStateInvalidException;
import info.zelazko.minibank.persistance.model.Transfer;
import lombok.Value;

import static info.zelazko.minibank.util.ErrorMessages.ERROR_CODE_TRANSFER_STATE_INVALID;
import static info.zelazko.minibank.util.ErrorMessages.ERROR_MSG_TRANSFER_STATE_INVALID;

@Value
public class TransferStatusValidator implements Validable {
    private final Transfer.Status status;

    @Override
    public void validate() {
        if (status == null || !Transfer.Status.INITIALIZED.equals(status)) {
            throw new TransferStateInvalidException(ERROR_MSG_TRANSFER_STATE_INVALID, ERROR_CODE_TRANSFER_STATE_INVALID);
        }
    }
}
