package info.zelazko.minibank.util;

import com.google.gson.Gson;
import info.zelazko.minibank.controller.response.ErrorResponse;
import info.zelazko.minibank.controller.response.ViewModel;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

import java.util.Optional;
import java.util.function.Function;

import static spark.Spark.exception;

@Slf4j
@UtilityClass
public class ResponseHelper {
    private static final String RESPONSE_EMPTY = "";
    private static final String LOG_REQUEST = "{} {} {}";
    private static final String LOG_RESPONSE = "Response status: {}, body: {}";

    public static String renderJson(Object model) {
        if (model != null) {
            return new Gson().toJson(model);
        }

        return RESPONSE_EMPTY;
    }

    public static <E extends Exception> void handleException(Class<E> exceptionClass, int statusCode, Function<E, ErrorResponse> handleResponse) {
        exception(exceptionClass, (exception, request, response) -> {
            log.warn(exception.getMessage(), exception);

            response.type(Web.MEDIA_TYPE_JSON);
            response.status(statusCode);
            response.body(RESPONSE_EMPTY);

            Optional.ofNullable(handleResponse.apply(exception))
                    .ifPresent(error -> response.body(renderJson(new ViewModel(error))));

        });
    }

    public static void logRequestInfo(Request request) {
        log.info(LOG_REQUEST, request.requestMethod(), request.url(), request.body());
    }

    public static void logResponseInfo(Response response) {
        log.info(LOG_RESPONSE, response.status(), response.body());
    }
}
