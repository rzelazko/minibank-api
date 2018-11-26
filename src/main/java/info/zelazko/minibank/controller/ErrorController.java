package info.zelazko.minibank.controller;

import com.google.common.net.MediaType;
import com.google.gson.JsonParseException;
import info.zelazko.minibank.controller.response.ErrorResponse;
import info.zelazko.minibank.controller.response.ViewModel;
import info.zelazko.minibank.exception.validation.ResourceNotFoundException;
import info.zelazko.minibank.exception.validation.TransferStateInvalidException;
import info.zelazko.minibank.exception.validation.ValidationException;
import info.zelazko.minibank.util.MinibankError;
import info.zelazko.minibank.util.SparkRunner;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Function;

import static java.net.HttpURLConnection.*;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static spark.Spark.*;

@Slf4j
public class ErrorController implements Controller {
    @Override
    public void initRouting() {
        // no routes - just error handling for Spark

        handleException(JsonParseException.class, HTTP_BAD_REQUEST, e -> new ErrorResponse(MinibankError.INVALID_JSON));
        handleException(TransferStateInvalidException.class, HTTP_NO_CONTENT, e -> null);
        handleException(ResourceNotFoundException.class, HTTP_NOT_FOUND, e -> new ErrorResponse(e.getMessage(), e.getCode()));
        handleException(ValidationException.class, HTTP_BAD_REQUEST, e -> new ErrorResponse(e.getMessage(), e.getCode()));

        notFound((request, response) -> SparkRunner.renderJson(new ErrorResponse(MinibankError.NOT_FOUND)));
        internalServerError((request, response) -> SparkRunner.renderJson(new ErrorResponse(MinibankError.DEFAULT)));
    }

    private <E extends Exception> void handleException(Class<E> exceptionClass, int statusCode, Function<E, ErrorResponse> handleResponse) {
        exception(exceptionClass, (exception, request, response) -> {
            log.warn(exception.getMessage(), exception);

            response.type(MediaType.JSON_UTF_8.toString());
            response.status(statusCode);

            Optional.ofNullable(handleResponse.apply(exception))
                    .ifPresent(error -> response.body(SparkRunner.renderJson(new ViewModel(error))));

        });
    }
}
