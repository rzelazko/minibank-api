package info.zelazko.minibank.controller;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import info.zelazko.minibank.controller.request.ConfirmCommand;
import info.zelazko.minibank.controller.request.InitializeCommand;
import info.zelazko.minibank.controller.response.TransferVM;
import info.zelazko.minibank.persistance.model.Transfer;
import info.zelazko.minibank.service.TransferService;
import info.zelazko.minibank.util.Mapping;
import info.zelazko.minibank.util.MappingHelper;
import info.zelazko.minibank.util.SparkRunner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import spark.Request;
import spark.Response;

import static java.net.HttpURLConnection.HTTP_ACCEPTED;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static spark.Spark.*;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class TransferController implements Controller {
    private final TransferService transferService;

    @Override
    public void initRouting() {
        post(Mapping.PATH_API_TRANSFERS, this::initializeTransfer, SparkRunner::renderJson);
        put(Mapping.PATH_API_TRANSFERS_UUID, this::authorizeTransfer, SparkRunner::renderJson);
        get(Mapping.PATH_API_TRANSFERS_UUID, this::getTransferDetails, SparkRunner::renderJson);
        delete(Mapping.PATH_API_TRANSFERS_UUID, this::deleteTransfer, SparkRunner::renderJson);
    }

    private TransferVM initializeTransfer(Request request, Response response) {
        Gson gson = new Gson();
        final InitializeCommand initializeCommand = gson.fromJson(request.body(), InitializeCommand.class);
        Transfer transfer = transferService.initialize(initializeCommand);
        TransferVM transferVM = new TransferVM(transfer);

        response.header(HttpHeaders.LOCATION, MappingHelper.parse(Mapping.PATH_API_TRANSFERS_UUID, transfer.getUuid()));
        response.status(HTTP_CREATED);
        return transferVM;
    }

    private TransferVM authorizeTransfer(Request request, Response response) {
        Gson gson = new Gson();
        final String uuid = request.params(MappingHelper.param(Mapping.PATH_API_TRANSFERS_UUID));
        final ConfirmCommand confirmCommand = gson.fromJson(request.body(), ConfirmCommand.class);
        transferService.confirm(uuid, confirmCommand);

        response.status(HTTP_ACCEPTED);
        return null;
    }

    private TransferVM getTransferDetails(Request request, Response response) {
        final String uuid = request.params(MappingHelper.param(Mapping.PATH_API_TRANSFERS_UUID));
        return new TransferVM(transferService.getTransfer(uuid));
    }

    private TransferVM deleteTransfer(Request request, Response response) {
        final String uuid = request.params(MappingHelper.param(Mapping.PATH_API_TRANSFERS_UUID));
        return new TransferVM(transferService.delete(uuid));
    }

}
