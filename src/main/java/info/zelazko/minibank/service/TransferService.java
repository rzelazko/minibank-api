package info.zelazko.minibank.service;

import info.zelazko.minibank.controller.request.ConfirmCommand;
import info.zelazko.minibank.controller.request.InitializeCommand;
import info.zelazko.minibank.exception.validation.ResourceNotFoundException;
import info.zelazko.minibank.persistance.MinibankDao;
import info.zelazko.minibank.persistance.model.Transfer;
import info.zelazko.minibank.validation.ConfirmCommandValidator;
import info.zelazko.minibank.validation.DeleteTransferValidator;
import info.zelazko.minibank.validation.InitializeCommandValidator;
import lombok.RequiredArgsConstructor;

import java.util.Currency;
import java.util.UUID;

import static info.zelazko.minibank.util.ErrorMessages.ERROR_CODE_TRANSFER_NOT_FOUND;
import static info.zelazko.minibank.util.ErrorMessages.ERROR_MSG_TRANSFER_NOT_FOUND;

@RequiredArgsConstructor
public class TransferService {
    private final MinibankDao minibankDao;

    public Transfer getTransfer(String uuid) {
        return minibankDao.findTransferByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ERROR_MSG_TRANSFER_NOT_FOUND, ERROR_CODE_TRANSFER_NOT_FOUND));
    }

    public Transfer initialize(InitializeCommand initializeCommand) {
        new InitializeCommandValidator(initializeCommand, minibankDao).validate();

        Transfer transfer = Transfer.builder()
                .uuid(UUID.randomUUID().toString())
                .status(Transfer.Status.INITIALIZED)
                .source(initializeCommand.getSource().toUpperCase())
                .destination(initializeCommand.getDestination().toUpperCase())
                .amount(initializeCommand.getAmount())
                .currency(Currency.getInstance(initializeCommand.getCurrency()))
                .build();

        return minibankDao.createTransfer(transfer);
    }

    public Transfer confirm(String uuid, ConfirmCommand confirmCommand) {
        Transfer transfer = getTransfer(uuid);
        new ConfirmCommandValidator(confirmCommand, transfer, minibankDao).validate();

        return minibankDao.confirmTransfer(transfer);
    }

    public Transfer delete(String uuid) {
        Transfer transfer = getTransfer(uuid);
        new DeleteTransferValidator(transfer).validate();

        return minibankDao.deleteTransfer(transfer.getUuid());
    }
}
