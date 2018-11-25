package info.zelazko.minibank.controller;

import com.google.gson.Gson;
import info.zelazko.minibank.controller.response.TransferVM;
import info.zelazko.minibank.util.Web;
import info.zelazko.minibank.controller.request.ConfirmCommand;
import info.zelazko.minibank.controller.request.InitializeCommand;
import info.zelazko.minibank.persistance.model.Transfer;
import info.zelazko.minibank.service.TransferService;
import info.zelazko.minibank.util.ResponseHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import spark.Request;
import spark.Response;

import static info.zelazko.minibank.util.Web.HEADER_HTTP_LOCATION;
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
        post(Web.PATH_API_TRANSFERS, this::initializeTransfer, ResponseHelper::renderJson);
        put(Web.PATH_API_TRANSFERS_UUID, this::authorizeTransfer, ResponseHelper::renderJson);
        get(Web.PATH_API_TRANSFERS_UUID, this::getTransferDetails, ResponseHelper::renderJson);
        delete(Web.PATH_API_TRANSFERS_UUID, this::deleteTransfer, ResponseHelper::renderJson);
    }

    private TransferVM initializeTransfer(Request request, Response response) {
        Gson gson = new Gson();
        final InitializeCommand initializeCommand = gson.fromJson(request.body(), InitializeCommand.class);
        Transfer transfer = transferService.initialize(initializeCommand);
        TransferVM transferVM = new TransferVM(transfer);

        response.header(HEADER_HTTP_LOCATION, Web.Path.parse(Web.PATH_API_TRANSFERS_UUID, transfer.getUuid()));
        response.status(HTTP_CREATED);
        return transferVM;
    }

    private TransferVM authorizeTransfer(Request request, Response response) {
        Gson gson = new Gson();
        final String uuid = request.params(Web.Path.param(Web.PATH_API_TRANSFERS_UUID));
        final ConfirmCommand confirmCommand = gson.fromJson(request.body(), ConfirmCommand.class);
        transferService.confirm(uuid, confirmCommand);

        response.status(HTTP_ACCEPTED);
        return null;
    }

    private TransferVM getTransferDetails(Request request, Response response) {
        final String uuid = request.params(Web.Path.param(Web.PATH_API_TRANSFERS_UUID));
        return new TransferVM(transferService.getTransfer(uuid));
    }

    private TransferVM deleteTransfer(Request request, Response response) {
        final String uuid = request.params(Web.Path.param(Web.PATH_API_TRANSFERS_UUID));
        return new TransferVM(transferService.delete(uuid));
    }

}
