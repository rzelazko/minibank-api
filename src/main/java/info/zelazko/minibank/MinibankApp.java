package info.zelazko.minibank;

import com.google.gson.JsonParseException;
import info.zelazko.minibank.controller.AccountController;
import info.zelazko.minibank.controller.Controller;
import info.zelazko.minibank.controller.TransferController;
import info.zelazko.minibank.exception.validation.ResourceNotFoundException;
import info.zelazko.minibank.exception.validation.TransferStateInvalidException;
import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.persistance.impl.inmemory.MinibankDaoInMemory;
import info.zelazko.minibank.service.AccountService;
import info.zelazko.minibank.service.TransferService;
import info.zelazko.minibank.controller.response.ErrorResponse;
import info.zelazko.minibank.util.Web;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static info.zelazko.minibank.util.ErrorMessages.*;
import static info.zelazko.minibank.util.ResponseHelper.*;
import static java.net.HttpURLConnection.*;
import static spark.Spark.*;

@Slf4j
public class MinibankApp {
    public static void main(String[] args) {
        port(Stream.of(args).findFirst().map(Integer::valueOf).orElse(8080));

        MinibankDao minibankDao = new MinibankDaoInMemory();
        AccountService accountService = new AccountService(minibankDao);
        TransferService transferService = new TransferService(minibankDao);

        List<Controller> controllers = Arrays.asList(
                new AccountController(accountService),
                new TransferController(transferService));
        controllers.stream().forEach(Controller::initRouting);

        before((request, response) -> logRequestInfo(request));

        handleException(JsonParseException.class, HTTP_BAD_REQUEST, e -> new ErrorResponse(ERROR_MSG_INVALID_JSON, ERROR_CODE_INVALID_JSON));
        handleException(TransferStateInvalidException.class, HTTP_NO_CONTENT, e -> null);
        handleException(ResourceNotFoundException.class, HTTP_NOT_FOUND, e -> new ErrorResponse(e.getMessage(), e.getCode()));
        handleException(ValidationException.class, HTTP_BAD_REQUEST, e -> new ErrorResponse(e.getMessage(), e.getCode()));

        notFound((request, response) -> renderJson(new ErrorResponse(ERROR_MSG_NOT_FOUND, ERROR_CODE_NOT_FOUND)));
        internalServerError((request, response) -> renderJson(new ErrorResponse(ERROR_MSG_DEFAULT, ERROR_CODE_DEFAULT)));

        after((request, response) -> response.type(Web.MEDIA_TYPE_JSON));
        after((request, response) -> logResponseInfo(response));
    }
}
