package info.zelazko.minibank.controller;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import info.zelazko.minibank.controller.request.AccountPayload;
import info.zelazko.minibank.controller.response.AccountVM;
import info.zelazko.minibank.persistance.model.Account;
import info.zelazko.minibank.service.AccountService;
import info.zelazko.minibank.util.Mapping;
import info.zelazko.minibank.util.MappingHelper;
import info.zelazko.minibank.util.SparkRunner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import spark.Request;
import spark.Response;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static spark.Spark.get;
import static spark.Spark.post;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class AccountController implements Controller {
    private final AccountService accountService;

    @Override
    public void initRouting() {
        post(Mapping.PATH_API_ACCOUNTS, this::createAccount, SparkRunner::renderJson);
        get(Mapping.PATH_API_ACCOUNTS_IBAN, this::getAccountByIban, SparkRunner::renderJson);
    }

    private AccountVM createAccount(Request request, Response response) {
        Gson gson = new Gson();
        AccountPayload accountPayload = gson.fromJson(request.body(), AccountPayload.class);
        Account account = accountService.createAccount(accountPayload);
        AccountVM accountVM = new AccountVM(account);

        response.header(HttpHeaders.LOCATION, MappingHelper.parse(Mapping.PATH_API_ACCOUNTS_IBAN, account.getIban()));
        response.status(HTTP_CREATED);
        return accountVM;
    }

    private AccountVM getAccountByIban(Request request, Response response) {
        String iban = request.params(MappingHelper.param(Mapping.PATH_API_ACCOUNTS_IBAN));
        return new AccountVM(accountService.getAccount(iban));
    }
}
