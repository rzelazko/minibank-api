package info.zelazko.minibank.controller;

import info.zelazko.minibank.controller.response.AccountVM;
import info.zelazko.minibank.controller.response.TransferVM;
import info.zelazko.minibank.persistance.model.Transfer;
import info.zelazko.minibank.util.MinibankError;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static info.zelazko.minibank.service.helper.MockValue.*;
import static java.net.HttpURLConnection.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
public class TransferControllerIT extends MinibankIntegrationTest {
    @Test
    void initializeThenGet() {
        // given
        createAccount(IBAN_XK, CURRENCY_CODE_EUR, AMOUNT_1000, HTTP_CREATED);
        createAccount(IBAN_LC, CURRENCY_CODE_EUR, AMOUNT_0, HTTP_CREATED);

        // when
        TransferVM transferInit = initializeTransfer(IBAN_XK, IBAN_LC, AMOUNT_500, CURRENCY_CODE_EUR, HTTP_CREATED);
        TransferVM transferRead = getTransfer(transferInit.getUuid());
        AccountVM sourceRead = getAccount(transferInit.getSource());
        AccountVM destinationRead = getAccount(transferInit.getDestination());

        // then
        assertEquals(IBAN_XK, transferRead.getSource());
        assertEquals(IBAN_LC, transferRead.getDestination());
        assertEquals(AMOUNT_500, transferRead.getAmount());
        assertEquals(CURRENCY_CODE_EUR, transferRead.getCurrency());
        assertEquals(Transfer.Status.INITIALIZED, transferRead.getStatus());
        assertEquals(AMOUNT_1000, sourceRead.getBalance());
        assertEquals(AMOUNT_0, destinationRead.getBalance());
    }

    @Test
    void initThenAuthorizeValid() {
        // given
        createAccount(IBAN_NL, CURRENCY_CODE_EUR, AMOUNT_1000, HTTP_CREATED);
        createAccount(IBAN_FR, CURRENCY_CODE_EUR, AMOUNT_0, HTTP_CREATED);
        TransferVM transferInit = initializeTransfer(IBAN_NL, IBAN_FR, AMOUNT_500, CURRENCY_CODE_EUR, HTTP_CREATED);

        // when
        authorizeTransfer(transferInit.getUuid(), AUTH_CODE_VALID, HTTP_ACCEPTED);
        TransferVM transferRead = getTransfer(transferInit.getUuid());
        AccountVM source = getAccount(transferRead.getSource());
        AccountVM destination = getAccount(transferRead.getDestination());

        // then
        assertEquals(IBAN_NL, transferRead.getSource());
        assertEquals(IBAN_FR, transferRead.getDestination());
        assertEquals(AMOUNT_500, transferRead.getAmount());
        assertEquals(CURRENCY_CODE_EUR, transferRead.getCurrency());
        assertEquals(Transfer.Status.INITIALIZED, transferInit.getStatus());
        assertEquals(Transfer.Status.CONFIRMED, transferRead.getStatus());
        assertEquals(AMOUNT_500, source.getBalance());
        assertEquals(AMOUNT_500, destination.getBalance());
    }

    @Test
    void initThenAuthorizeInValid() {
        // given
        createAccount(IBAN_UA, CURRENCY_CODE_EUR, AMOUNT_1000, HTTP_CREATED);
        createAccount(IBAN_PS, CURRENCY_CODE_EUR, AMOUNT_0, HTTP_CREATED);
        TransferVM transferInit = initializeTransfer(IBAN_UA, IBAN_PS, AMOUNT_500, CURRENCY_CODE_EUR, HTTP_CREATED);

        // when
        authorizeTransfer(transferInit.getUuid(), AUTH_CODE_INVALID, HTTP_BAD_REQUEST);
        TransferVM transferRead = getTransfer(transferInit.getUuid());
        AccountVM source = getAccount(transferRead.getSource());
        AccountVM destination = getAccount(transferRead.getDestination());

        // then
        assertEquals(IBAN_UA, transferRead.getSource());
        assertEquals(IBAN_PS, transferRead.getDestination());
        assertEquals(AMOUNT_500, transferRead.getAmount());
        assertEquals(CURRENCY_CODE_EUR, transferRead.getCurrency());
        assertEquals(Transfer.Status.INITIALIZED, transferRead.getStatus());
        assertEquals(AMOUNT_1000, source.getBalance());
        assertEquals(AMOUNT_0, destination.getBalance());
    }

    @Test
    void initThenAuthorizeInvalidThenValid() {
        // given
        createAccount(IBAN_MT, CURRENCY_CODE_EUR, AMOUNT_1000, HTTP_CREATED);
        createAccount(IBAN_HU, CURRENCY_CODE_EUR, AMOUNT_0, HTTP_CREATED);
        TransferVM transferInit = initializeTransfer(IBAN_MT, IBAN_HU, AMOUNT_500, CURRENCY_CODE_EUR, HTTP_CREATED);

        // when
        authorizeTransfer(transferInit.getUuid(), AUTH_CODE_INVALID, HTTP_BAD_REQUEST);
        authorizeTransfer(transferInit.getUuid(), AUTH_CODE_VALID, HTTP_ACCEPTED);
        TransferVM transferRead = getTransfer(transferInit.getUuid());
        AccountVM source = getAccount(transferRead.getSource());
        AccountVM destination = getAccount(transferRead.getDestination());

        // then
        assertEquals(IBAN_MT, transferRead.getSource());
        assertEquals(IBAN_HU, transferRead.getDestination());
        assertEquals(AMOUNT_500, transferRead.getAmount());
        assertEquals(CURRENCY_CODE_EUR, transferRead.getCurrency());
        assertEquals(Transfer.Status.INITIALIZED, transferInit.getStatus());
        assertEquals(Transfer.Status.CONFIRMED, transferRead.getStatus());
        assertEquals(AMOUNT_500, source.getBalance());
        assertEquals(AMOUNT_500, destination.getBalance());
    }

    @Test
    void initThenDeleteThenAuthorize() {
        // given
        createAccount(IBAN_EE, CURRENCY_CODE_EUR, AMOUNT_1000, HTTP_CREATED);
        createAccount(IBAN_BF, CURRENCY_CODE_EUR, AMOUNT_0, HTTP_CREATED);
        TransferVM transferInit = initializeTransfer(IBAN_MT, IBAN_HU, AMOUNT_500, CURRENCY_CODE_EUR, HTTP_CREATED);

        // when
        deleteTransfer(transferInit.getUuid(), HTTP_OK);
        authorizeTransfer(transferInit.getUuid(), AUTH_CODE_VALID, HTTP_NOT_FOUND);
        AccountVM source = getAccount(transferInit.getSource());
        AccountVM destination = getAccount(transferInit.getDestination());

        // then
        assertEquals(AMOUNT_500, source.getBalance());
        assertEquals(AMOUNT_500, destination.getBalance());
    }

    @Test
    void initThenDeleteTwice() {
        // given
        createAccount(IBAN_BH, CURRENCY_CODE_EUR, AMOUNT_1000, HTTP_CREATED);
        createAccount(IBAN_BE, CURRENCY_CODE_EUR, AMOUNT_0, HTTP_CREATED);
        TransferVM transferInit = initializeTransfer(IBAN_BH, IBAN_BE, AMOUNT_500, CURRENCY_CODE_EUR, HTTP_CREATED);

        // when
        deleteTransfer(transferInit.getUuid(), HTTP_OK);
        deleteTransfer(transferInit.getUuid(), HTTP_NOT_FOUND);
        AccountVM source = getAccount(transferInit.getSource());
        AccountVM destination = getAccount(transferInit.getDestination());

        // then
        assertEquals(AMOUNT_1000, source.getBalance());
        assertEquals(AMOUNT_0, destination.getBalance());
    }

    @Test
    void initializeNoFounds() {
        // given
        AccountVM sourceCreate = createAccount(IBAN_BR, CURRENCY_CODE_EUR, AMOUNT_500, HTTP_CREATED);
        AccountVM destCreate = createAccount(IBAN_BJ, CURRENCY_CODE_EUR, AMOUNT_0, HTTP_CREATED);

        // when
        TransferVM transferInit = initializeTransfer(IBAN_BR, IBAN_BJ, AMOUNT_1000, CURRENCY_CODE_EUR, HTTP_BAD_REQUEST);
        AccountVM source = getAccount(sourceCreate.getIban());
        AccountVM destination = getAccount(destCreate.getIban());

        // then
        assertNotNull(transferInit.getError());
        assertEquals(MinibankError.INVALID_BALANCE.getCode(), transferInit.getError().getCode());
        assertEquals(MinibankError.INVALID_BALANCE.getMessage(), transferInit.getError().getMessage());
        assertEquals(AMOUNT_500, source.getBalance());
        assertEquals(AMOUNT_0, destination.getBalance());
    }
}
