package info.zelazko.minibank.util;

import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import info.zelazko.minibank.controller.AccountController;
import info.zelazko.minibank.controller.Controller;
import info.zelazko.minibank.controller.ErrorController;
import info.zelazko.minibank.controller.TransferController;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.persistance.impl.inmemory.MinibankDaoInMemory;
import info.zelazko.minibank.service.AccountService;
import info.zelazko.minibank.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

import java.util.List;

import static spark.Spark.*;

@Slf4j
public class SparkRunner {
    public static final int DEFAULT_PORT = 8080;
    public static final int RANDOM_PORT = 0;

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
                new TransferController(transferService),
                new ErrorController());
    }

    private void controllers() {
        port(this.port);
        controllers.stream().forEach(Controller::initRouting);
    }

    private void filters() {
        before((request, response) -> logRequestInfo(request));
        after((request, response) -> response.type(MediaType.JSON_UTF_8.toString()));
        after((request, response) -> logResponseInfo(response));
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
        app.controllers();
        app.filters();
    }
}
