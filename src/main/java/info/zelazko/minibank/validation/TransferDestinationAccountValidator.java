package info.zelazko.minibank.validation;

import info.zelazko.minibank.exception.validation.ResourceNotFoundException;
import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.persistance.model.Account;
import lombok.Value;

import java.util.Currency;

import static info.zelazko.minibank.util.ErrorMessages.*;

@Value
public class TransferDestinationAccountValidator implements Validable {
    private final String accountIban;
    private final String withdrawCurrencyCode;
    private final MinibankDao dao;

    @Override
    public void validate() {
        Account destination = dao.findAccountByIban(accountIban)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ERROR_MSG_ACCOUNT_NOT_FOUND, accountIban), ERROR_CODE_ACCOUNT_NOT_FOUND));

        Currency withdrawCurrency = Currency.getInstance(withdrawCurrencyCode);
        if (withdrawCurrency == null || !withdrawCurrency.equals(destination.getCurrency())) {
            throw new ValidationException(ERROR_MSG_TRANSFER_CURRENCY_INVALID, ERROR_CODE_TRANSFER_CURRENCY_DESTINATION_MISMATCH);
        }
    }

}
