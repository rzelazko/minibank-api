package info.zelazko.minibank.controller;

import info.zelazko.minibank.controller.request.AccountPayload;
import info.zelazko.minibank.controller.response.AccountVM;
import info.zelazko.minibank.util.Mapping;
import info.zelazko.minibank.util.MappingHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static info.zelazko.minibank.service.helper.MockValue.*;
import static info.zelazko.minibank.util.ErrorMessages.*;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
class AccountControllerIT extends MinibankIntegrationTest {

    @Test
    void createAndGetAccount() {
        // when
        AccountVM accountCreated = createAccount(IBAN_PL, CURRENCY_CODE_PLN, AMOUNT_1000, HTTP_CREATED);
        AccountVM accountRead = getAccount(accountCreated.getIban());

        // then
        assertEquals(IBAN_PL, accountRead.getIban());
        assertEquals(CURRENCY_CODE_PLN, accountRead.getCurrency());
        assertEquals(AMOUNT_1000, accountRead.getBalance());
        assertEquals(accountRead, accountCreated);
    }

    @Test
    void getAccountInvalid() {
        // when
        AccountVM accountRead = given().request()
                .when().get(MappingHelper.parse(Mapping.PATH_API_ACCOUNTS_IBAN, IBAN_INVALID))
                .then().statusCode(HTTP_BAD_REQUEST).extract().as(AccountVM.class);

        // then
        assertNotNull(accountRead.getError());
        assertEquals(ERROR_CODE_INVALID_IBAN, accountRead.getError().getCode());
        assertEquals(String.format(ERROR_MSG_INVALID_IBAN, IBAN_INVALID), accountRead.getError().getMessage());
    }

    @Test
    void createAccountInvalid() {
        // when
        AccountVM accountCreated = createAccount(IBAN_INVALID, CURRENCY_CODE_PLN, AMOUNT_1000, HTTP_BAD_REQUEST);

        // then
        assertNotNull(accountCreated.getError());
        assertEquals(ERROR_CODE_INVALID_IBAN, accountCreated.getError().getCode());
        assertEquals(String.format(ERROR_MSG_INVALID_IBAN, IBAN_INVALID), accountCreated.getError().getMessage());
    }

    @Test
    void createAccountDuplicate() {
        // given
        AccountVM accountCreated1 = createAccount(IBAN_GB_LOWCASE, CURRENCY_CODE_PLN, AMOUNT_1000, HTTP_CREATED);
        AccountPayload payload = AccountPayload.builder()
                .iban(IBAN_GB)
                .currency(CURRENCY_CODE_PLN)
                .balance(AMOUNT_1000)
                .build();

        // when
        AccountVM accountCreated2 = given().request().body(payload)
                .when().post(Mapping.PATH_API_ACCOUNTS)
                .then().statusCode(HTTP_BAD_REQUEST).extract().as(AccountVM.class);

        // then
        assertEquals(IBAN_GB, accountCreated1.getIban());
        assertNotNull(accountCreated2.getError());
        assertEquals(ERROR_CODE_ACCOUNT_EXISTS, accountCreated2.getError().getCode());
        assertEquals(String.format(ERROR_MSG_ACCOUNT_EXISTS, IBAN_GB), accountCreated2.getError().getMessage());
    }
}
