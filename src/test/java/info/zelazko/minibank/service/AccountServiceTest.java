package info.zelazko.minibank.service;

import info.zelazko.minibank.controller.request.AccountPayload;
import info.zelazko.minibank.exception.validation.ResourceNotFoundException;
import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.model.Account;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.service.helper.CreateAccountProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Optional;

import static info.zelazko.minibank.service.helper.MockValue.*;
import static info.zelazko.minibank.util.ErrorMessages.ERROR_CODE_ACCOUNT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AccountServiceTest {
    private MinibankDao minibankDao;

    @BeforeEach
    void init() {
        minibankDao = mock(MinibankDao.class);
    }

    @Test
    void getAccountValid() {
        // given
        Optional<Account> account = Optional.of(Account.builder()
                .iban(IBAN_PL)
                .currency(CURRENCY_PLN)
                .balance(AMOUNT_1000)
                .build());
        when(minibankDao.findAccountByIban(anyString())).thenReturn(account);

        // when
        AccountService accountService = new AccountService(minibankDao);
        Account result = accountService.getAccount(IBAN_PL);

        // then
        assertTrue(result.equals(account.get()));
    }

    @Test
    void getAccountNotFound() {
        // given
        Optional<Account> account = Optional.empty();
        when(minibankDao.findAccountByIban(anyString())).thenReturn(account);

        // when
        AccountService accountService = new AccountService(minibankDao);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> accountService.getAccount(IBAN_PL));

        // then
        assertEquals(ERROR_CODE_ACCOUNT_NOT_FOUND, exception.getCode());
    }

    @Test
    void createAccountValid() {
        // given
        AccountPayload accountPayload = AccountPayload.builder()
                .iban(IBAN_PL)
                .currency(CURRENCY_PLN.getCurrencyCode())
                .balance(AMOUNT_1000)
                .build();
        Account account = Account.builder()
                .iban(IBAN_PL)
                .currency(CURRENCY_PLN)
                .balance(AMOUNT_1000)
                .build();
        when(minibankDao.createAccount(any(Account.class))).thenReturn(account);

        // when
        AccountService accountService = new AccountService(minibankDao);
        Account result = accountService.createAccount(accountPayload);

        // then
        verify(minibankDao, times(1)).createAccount(eq(account));
        assertTrue(result.equals(account));
    }

    @DisplayName("createAccount Validation")
    @ParameterizedTest(name = "Scenario: {0}, expected {1}")
    @ArgumentsSource(CreateAccountProvider.class)
    void createAccountValidationException(String scenario, String errorCode, Optional<Account> account, AccountPayload accountPayload) {
        // given - accountPayload & account
        account.ifPresent(a -> when(minibankDao.findAccountByIban(eq(a.getIban()))).thenReturn(account));

        // when
        AccountService accountService = new AccountService(minibankDao);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> accountService.createAccount(accountPayload));

        // then
        verify(minibankDao, never()).createAccount(any(Account.class));
        assertEquals(errorCode, exception.getCode());
    }
}
