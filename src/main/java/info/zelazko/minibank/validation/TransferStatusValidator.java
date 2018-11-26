package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.TransferStateInvalidException;
import info.zelazko.minibank.persistance.model.Transfer;
import info.zelazko.minibank.util.MinibankError;
import lombok.Value;

@Value
public class TransferStatusValidator implements Validable {
    private final Transfer.Status status;

    @Override
    public void validate() {
        if (status == null || !Transfer.Status.INITIALIZED.equals(status)) {
            throw new TransferStateInvalidException(MinibankError.TRANSFER_STATE_INVALID);
        }
    }
}
