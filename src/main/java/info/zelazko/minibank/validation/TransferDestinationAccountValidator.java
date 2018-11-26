package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ResourceNotFoundException;
import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.persistance.model.Account;
import info.zelazko.minibank.util.MinibankError;
import lombok.Value;

import java.util.Currency;

@Value
public class TransferDestinationAccountValidator implements Validable {
    private final String accountIban;
    private final String withdrawCurrencyCode;
    private final MinibankDao dao;

    @Override
    public void validate() {
        Account destination = dao.findAccountByIban(accountIban)
                .orElseThrow(() -> new ResourceNotFoundException(MinibankError.ACCOUNT_NOT_FOUND, accountIban));

        Currency withdrawCurrency = Currency.getInstance(withdrawCurrencyCode);
        if (withdrawCurrency == null || !withdrawCurrency.equals(destination.getCurrency())) {
            throw new ValidationException(MinibankError.TRANSFER_CURRENCY_DESTINATION_MISMATCH);
        }
    }

}
