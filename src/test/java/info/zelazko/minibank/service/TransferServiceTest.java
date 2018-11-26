package info.zelazko.minibank.service;

import info.zelazko.minibank.controller.request.ConfirmCommand;
import info.zelazko.minibank.controller.request.InitializeCommand;
import info.zelazko.minibank.exception.validation.ResourceNotFoundException;
import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.persistance.model.Account;
import info.zelazko.minibank.persistance.model.Transfer;
import info.zelazko.minibank.service.helper.ConfirmTransferProvider;
import info.zelazko.minibank.service.helper.InitializeTransferProvider;
import info.zelazko.minibank.service.helper.MockBuilder;
import info.zelazko.minibank.service.helper.MockValue;
import info.zelazko.minibank.util.MinibankError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {
    private MinibankDao minibankDao;

    @BeforeEach
    void init() {
        minibankDao = mock(MinibankDao.class);
    }

    @Test
    void getTransferValid() {
        // given
        Optional<Transfer> transfer = MockBuilder.prepareEurTransfer(MockValue.IBAN_PL, MockValue.IBAN_GB, MockValue.AMOUNT_500);
        when(minibankDao.findTransferByUuid(anyString())).thenReturn(transfer);

        // when
        TransferService transferService = new TransferService(minibankDao);
        Transfer result = transferService.getTransfer(MockValue.UUID1);

        // then
        assertTrue(result.equals(transfer.get()));
    }

    @Test
    void getTransferNotFound() {
        // given
        Optional<Transfer> transfer = Optional.empty();
        when(minibankDao.findTransferByUuid(anyString())).thenReturn(transfer);

        // when
        TransferService transferService = new TransferService(minibankDao);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> transferService.getTransfer(MockValue.UUID1));

        // then
        assertEquals(MinibankError.TRANSFER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void initializeTransferValid() {
        // given
        InitializeCommand initializeCommand = InitializeCommand.builder()
                .source(MockValue.IBAN_PL)
                .destination(MockValue.IBAN_GB)
                .amount(MockValue.AMOUNT_500)
                .currency(MockValue.CURRENCY_CODE_EUR)
                .build();
        Optional<Transfer> transfer = MockBuilder.prepareEurTransfer(MockValue.IBAN_PL, MockValue.IBAN_GB, MockValue.AMOUNT_500);
        Optional<Account> sourceAccount = MockBuilder.prepareEurAccount(MockValue.IBAN_PL, MockValue.AMOUNT_1000);
        Optional<Account> destinationAccount = MockBuilder.prepareEurAccount(MockValue.IBAN_GB, MockValue.AMOUNT_M100);
        when(minibankDao.findAccountByIban(eq(sourceAccount.get().getIban()))).thenReturn(sourceAccount);
        when(minibankDao.findAccountByIban(eq(destinationAccount.get().getIban()))).thenReturn(destinationAccount);
        when(minibankDao.findTransferByUuid(eq(transfer.get().getUuid()))).thenReturn(transfer);
        when(minibankDao.createTransfer(any(Transfer.class))).thenReturn(transfer.get());

        // when
        TransferService transferService = new TransferService(minibankDao);
        Transfer result = transferService.initialize(initializeCommand);

        // then
        assertTrue(transfer.get().equals(result));
    }

    @DisplayName("initializeTransfer Validation")
    @ParameterizedTest(name = "Scenario: {0}, expected {1}")
    @ArgumentsSource(InitializeTransferProvider.class)
    void initializeTransferValidationException(String scenario, MinibankError error, Optional<Account> sourceAccount,
                                               Optional<Account> destinationAccount, InitializeCommand initializeCommand) {
        // given - initializeCommand & sourceAccount & destinationAccount
        sourceAccount.ifPresent(account -> when(minibankDao.findAccountByIban(eq(account.getIban()))).thenReturn(sourceAccount));
        destinationAccount.ifPresent(account -> when(minibankDao.findAccountByIban(eq(account.getIban()))).thenReturn(destinationAccount));

        // when
        TransferService transferService = new TransferService(minibankDao);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> transferService.initialize(initializeCommand));

        // then
        assertEquals(error.getCode(), exception.getCode());
    }

    @Test
    void confirmTransferValid() {
        // given
        String uuid = MockValue.UUID1;
        ConfirmCommand confirmCommand = new ConfirmCommand(MockValue.AUTH_CODE_VALID);
        Optional<Transfer> transfer = MockBuilder.prepareEurTransfer(MockValue.IBAN_PL, MockValue.IBAN_GB, MockValue.AMOUNT_500);
        Optional<Account> sourceAccount = MockBuilder.prepareEurAccount(MockValue.IBAN_PL, MockValue.AMOUNT_1000);
        Optional<Account> destinationAccount = MockBuilder.prepareEurAccount(MockValue.IBAN_GB, MockValue.AMOUNT_0);
        when(minibankDao.findAccountByIban(eq(sourceAccount.get().getIban()))).thenReturn(sourceAccount);
        when(minibankDao.findAccountByIban(eq(destinationAccount.get().getIban()))).thenReturn(destinationAccount);
        when(minibankDao.findTransferByUuid(eq(transfer.get().getUuid()))).thenReturn(transfer);

        // when
        TransferService transferService = new TransferService(minibankDao);
        transferService.confirm(uuid, confirmCommand);

        // then
        verify(minibankDao, times(1)).confirmTransfer(transfer.get());
    }

    @DisplayName("confirmTransfer Validation")
    @ParameterizedTest(name = "Scenario: {0}, expected {1}")
    @ArgumentsSource(ConfirmTransferProvider.class)
    void confirmTransferValidationException(String scenario, MinibankError error, String uuid, Optional<Account> sourceAccount,
                                            Optional<Account> destinationAccount, Optional<Transfer> transfer, ConfirmCommand confirmCommand) {
        // given
        sourceAccount.ifPresent(account -> when(minibankDao.findAccountByIban(eq(account.getIban()))).thenReturn(sourceAccount));
        destinationAccount.ifPresent(account -> when(minibankDao.findAccountByIban(eq(account.getIban()))).thenReturn(destinationAccount));
        transfer.ifPresent(t -> when(minibankDao.findTransferByUuid(eq(t.getUuid()))).thenReturn(transfer));

        // when
        TransferService transferService = new TransferService(minibankDao);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> transferService.confirm(uuid, confirmCommand));

        // then
        verify(minibankDao, never()).confirmTransfer(any(Transfer.class));
        assertEquals(error.getCode(), exception.getCode());
    }

    @Test
    void deleteTransferValid() {
        // given
        Optional<Transfer> transfer = MockBuilder.prepareEurTransfer(MockValue.IBAN_PL, MockValue.IBAN_GB, MockValue.AMOUNT_500);
        when(minibankDao.findTransferByUuid(anyString())).thenReturn(transfer);

        // when
        TransferService transferService = new TransferService(minibankDao);
        transferService.delete(MockValue.UUID1);

        // then
        verify(minibankDao, times(1)).deleteTransfer(eq(MockValue.UUID1));
    }

    @Test
    void deleteTransferNotFound() {
        // given
        Optional<Transfer> transfer = Optional.empty();
        when(minibankDao.findTransferByUuid(anyString())).thenReturn(transfer);

        // when
        TransferService transferService = new TransferService(minibankDao);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> transferService.delete(MockValue.UUID1));

        // then
        verify(minibankDao, never()).deleteTransfer(anyString());
        assertEquals(MinibankError.TRANSFER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void deleteTransferInvalidState() {
        // given
        Optional<Transfer> transfer = MockBuilder.prepareEurTransfer(MockValue.IBAN_PL, MockValue.IBAN_GB, MockValue.AMOUNT_500, Transfer.Status.CONFIRMED);
        when(minibankDao.findTransferByUuid(anyString())).thenReturn(transfer);

        // when
        TransferService transferService = new TransferService(minibankDao);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> transferService.delete(MockValue.UUID1));

        // then
        verify(minibankDao, never()).deleteTransfer(anyString());
        assertEquals(MinibankError.DEL_NONINITIALIZED.getCode(), exception.getCode());
    }

}
