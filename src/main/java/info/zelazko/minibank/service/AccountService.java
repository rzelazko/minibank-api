package info.zelazko.minibank.service;

import info.zelazko.minibank.controller.request.AccountPayload;
import info.zelazko.minibank.exception.validation.ResourceNotFoundException;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.persistance.model.Account;
import info.zelazko.minibank.util.MinibankError;
import info.zelazko.minibank.validation.AccountPayloadValidator;
import info.zelazko.minibank.validation.IbanValidator;
import lombok.RequiredArgsConstructor;

import java.util.Currency;

@RequiredArgsConstructor
public class AccountService {
    private final MinibankDao minibankDao;

    public Account getAccount(String iban) {
        new IbanValidator(iban).validate();

        return minibankDao.findAccountByIban(iban)
                .orElseThrow(() -> new ResourceNotFoundException(MinibankError.ACCOUNT_NOT_FOUND, iban));
    }

    public Account createAccount(AccountPayload accountPayload) {
        new AccountPayloadValidator(accountPayload, minibankDao).validate();

        Account account = Account.builder()
                .balance(accountPayload.getBalance())
                .currency(Currency.getInstance(accountPayload.getCurrency().toUpperCase()))
                .iban(accountPayload.getIban().toUpperCase())
                .build();

        return minibankDao.createAccount(account);
    }
}
