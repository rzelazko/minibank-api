package info.zelazko.minibank.service;

import info.zelazko.minibank.controller.request.AccountPayload;
import info.zelazko.minibank.validation.AccountPayloadValidator;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.persistance.model.Account;
import info.zelazko.minibank.exception.validation.ResourceNotFoundException;
import info.zelazko.minibank.validation.Validable;
import lombok.RequiredArgsConstructor;

import java.util.Currency;
import java.util.Optional;

import static info.zelazko.minibank.util.ErrorMessages.ERROR_CODE_ACCOUNT_NOT_FOUND;
import static info.zelazko.minibank.util.ErrorMessages.ERROR_MSG_ACCOUNT_NOT_FOUND;

@RequiredArgsConstructor
public class AccountService {
    private final MinibankDao minibankDao;

    public Account getAccount(String iban) {
        String normalizedIban = Optional.ofNullable(iban).orElse("").toUpperCase();
        return minibankDao.findAccountByIban(normalizedIban)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ERROR_MSG_ACCOUNT_NOT_FOUND, iban), ERROR_CODE_ACCOUNT_NOT_FOUND));
    }

    public Account createAccount(AccountPayload accountPayload) {
        Validable requestValidator = new AccountPayloadValidator(accountPayload, minibankDao);
        requestValidator.validate();

        Account account = Account.builder()
                .balance(accountPayload.getBalance())
                .currency(Currency.getInstance(accountPayload.getCurrency().toUpperCase()))
                .iban(accountPayload.getIban().toUpperCase())
                .build();

        return minibankDao.createAccount(account);
    }
}
