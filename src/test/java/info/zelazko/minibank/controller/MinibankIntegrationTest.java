package info.zelazko.minibank.controller;

import info.zelazko.minibank.MinibankApp;
import info.zelazko.minibank.controller.request.AccountPayload;
import info.zelazko.minibank.controller.request.ConfirmCommand;
import info.zelazko.minibank.controller.request.InitializeCommand;
import info.zelazko.minibank.controller.response.AccountVM;
import info.zelazko.minibank.controller.response.TransferVM;
import info.zelazko.minibank.util.Web;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import spark.Spark;

import static info.zelazko.minibank.util.Web.*;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;

abstract class MinibankIntegrationTest {
    @BeforeAll
    static void beforeAll() {
        MinibankApp.main(new String[]{"0"});
        Spark.awaitInitialization();

        RestAssured.port = Spark.port();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Spark.stop();
        Thread.sleep(1000);
    }

    AccountVM createAccount(String iban, String currency, int balance, int httpStatus) {
        AccountPayload payload = AccountPayload.builder()
                .iban(iban)
                .currency(currency)
                .balance(balance)
                .build();

        AccountVM account = given().request().body(payload)
                .when().post(PATH_API_ACCOUNTS)
                .then().statusCode(httpStatus).extract().as(AccountVM.class);

        return account;
    }

    AccountVM getAccount(String iban) {
        return given().request()
                .when().get(Web.Path.parse(PATH_API_ACCOUNTS_IBAN, iban))
                .then().statusCode(HTTP_OK).extract().as(AccountVM.class);
    }

    TransferVM initializeTransfer(String source, String destination, int amount, String currency, int httpStatus) {
        InitializeCommand payload = InitializeCommand.builder()
                .source(source)
                .destination(destination)
                .amount(amount)
                .currency(currency)
                .build();

        TransferVM transferCreated = given().request().body(payload)
                .when().post(PATH_API_TRANSFERS)
                .then().statusCode(httpStatus).extract().as(TransferVM.class);

        return transferCreated;
    }

    TransferVM authorizeTransfer(String uuid, String authCode, int httpStatus) {
        ConfirmCommand payload = new ConfirmCommand(authCode);

        return given().request().body(payload)
                .when().put(Path.parse(PATH_API_TRANSFERS_UUID, uuid))
                .then().statusCode(httpStatus).extract().as(TransferVM.class);
    }

    TransferVM getTransfer(String uuid) {
        return given().request()
                .when().get(Web.Path.parse(PATH_API_TRANSFERS_UUID, uuid))
                .then().statusCode(HTTP_OK).extract().as(TransferVM.class);
    }

    TransferVM deleteTransfer(String uuid, int httpStatus) {
        return given().request()
                .when().delete(Web.Path.parse(PATH_API_TRANSFERS_UUID, uuid))
                .then().statusCode(httpStatus).extract().as(TransferVM.class);
    }
}
