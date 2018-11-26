package info.zelazko.minibank.util;

import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import info.zelazko.minibank.controller.AccountController;
import info.zelazko.minibank.controller.Controller;
import info.zelazko.minibank.controller.TransferController;
import info.zelazko.minibank.controller.response.ErrorResponse;
import info.zelazko.minibank.controller.response.ViewModel;
import info.zelazko.minibank.exception.validation.ResourceNotFoundException;
import info.zelazko.minibank.exception.validation.TransferStateInvalidException;
import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.persistance.impl.inmemory.MinibankDaoInMemory;
import info.zelazko.minibank.service.AccountService;
import info.zelazko.minibank.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static info.zelazko.minibank.util.ErrorMessages.*;
import static java.net.HttpURLConnection.*;
import static spark.Spark.*;

@Slf4j
public class SparkRunner {
    public static final int DEFAULT_PORT = 8080;
    public static final int RANDOM_PORT = 0;

    private static final String MEDIA_TYPE_JSON = "application/json; charset=utf-8";
    private static final String RESPONSE_EMPTY = "";
    private static final String LOG_REQUEST = "{} {} {}";
    private static final String LOG_RESPONSE = "Response status: {}, body: {}";

    private final int port;

    private final List<Controller> controllers;

    private SparkRunner(int port) {
        this.port = port;

        MinibankDao minibankDao = new MinibankDaoInMemory();
        AccountService accountService = new AccountService(minibankDao);
        TransferService transferService = new TransferService(minibankDao);

        controllers = ImmutableList.of(
                new AccountController(accountService),
                new TransferController(transferService));
    }

    private void routing() {
        port(this.port);
        controllers.stream().forEach(Controller::initRouting);
    }

    private void filters() {
        before((request, response) -> logRequestInfo(request));
        after((request, response) -> response.type(MEDIA_TYPE_JSON));
        after((request, response) -> logResponseInfo(response));
    }

    private void exceptionHanling() {
        before((request, response) -> logRequestInfo(request));
        after((request, response) -> response.type(MEDIA_TYPE_JSON));
        after((request, response) -> logResponseInfo(response));

        handleException(JsonParseException.class, HTTP_BAD_REQUEST, e -> new ErrorResponse(ERROR_MSG_INVALID_JSON, ERROR_CODE_INVALID_JSON));
        handleException(TransferStateInvalidException.class, HTTP_NO_CONTENT, e -> null);
        handleException(ResourceNotFoundException.class, HTTP_NOT_FOUND, e -> new ErrorResponse(e.getMessage(), e.getCode()));
        handleException(ValidationException.class, HTTP_BAD_REQUEST, e -> new ErrorResponse(e.getMessage(), e.getCode()));

        notFound((request, response) -> renderJson(new ErrorResponse(ERROR_MSG_NOT_FOUND, ERROR_CODE_NOT_FOUND)));
        internalServerError((request, response) -> renderJson(new ErrorResponse(ERROR_MSG_DEFAULT, ERROR_CODE_DEFAULT)));
    }

    private <E extends Exception> void handleException(Class<E> exceptionClass, int statusCode, Function<E, ErrorResponse> handleResponse) {
        exception(exceptionClass, (exception, request, response) -> {
            log.warn(exception.getMessage(), exception);

            response.type(MediaType.JSON_UTF_8.toString());
            response.status(statusCode);
            response.body(RESPONSE_EMPTY);

            Optional.ofNullable(handleResponse.apply(exception))
                    .ifPresent(error -> response.body(renderJson(new ViewModel(error))));

        });
    }

    private void logRequestInfo(Request request) {
        log.info(LOG_REQUEST, request.requestMethod(), request.url(), request.body());
    }

    private void logResponseInfo(Response response) {
        log.info(LOG_RESPONSE, response.status(), response.body());
    }

    public static String renderJson(Object model) {
        if (model != null) {
            return new Gson().toJson(model);
        }

        return RESPONSE_EMPTY;
    }

    public static void run(int port) {
        SparkRunner app = new SparkRunner(port);

        app.routing();
        app.filters();
        app.exceptionHanling();
    }
}
