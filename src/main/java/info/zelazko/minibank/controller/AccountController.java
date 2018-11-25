package info.zelazko.minibank.controller;

import com.google.gson.Gson;
import info.zelazko.minibank.controller.request.AccountPayload;
import info.zelazko.minibank.controller.response.AccountVM;
import info.zelazko.minibank.controller.response.ViewModel;
import info.zelazko.minibank.persistance.model.Account;
import info.zelazko.minibank.service.AccountService;
import info.zelazko.minibank.util.ResponseHelper;
import info.zelazko.minibank.util.Web;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

import static info.zelazko.minibank.util.Web.*;
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
        post(PATH_API_ACCOUNTS, this::createAccount, ResponseHelper::renderJson);
        get(PATH_API_ACCOUNTS_IBAN, this::getAccountByIban, ResponseHelper::renderJson);
    }

    private AccountVM createAccount(Request request, Response response) {
        Gson gson = new Gson();
        AccountPayload accountPayload = gson.fromJson(request.body(), AccountPayload.class);
        Account account = accountService.createAccount(accountPayload);
        AccountVM accountVM = new AccountVM(account);

        response.header(HEADER_HTTP_LOCATION, Web.Path.parse(PATH_API_ACCOUNTS_IBAN, account.getIban()));
        response.status(HTTP_CREATED);
        return accountVM;
    }

    private AccountVM getAccountByIban(Request request, Response response) {
        String iban = request.params(Web.Path.param(PATH_API_ACCOUNTS_IBAN));
        return new AccountVM(accountService.getAccount(iban));
    }
}
