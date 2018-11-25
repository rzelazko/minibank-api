package info.zelazko.minibank.service.helper;

import info.zelazko.minibank.controller.request.ConfirmCommand;
import info.zelazko.minibank.persistance.model.Transfer;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Optional;
import java.util.stream.Stream;

import static info.zelazko.minibank.service.helper.MockBuilder.prepareEurAccount;
import static info.zelazko.minibank.service.helper.MockBuilder.prepareEurTransfer;
import static info.zelazko.minibank.service.helper.MockBuilder.preparePlnAccount;
import static info.zelazko.minibank.service.helper.MockValue.*;
import static info.zelazko.minibank.util.ErrorMessages.*;
import static info.zelazko.minibank.util.ErrorMessages.ERROR_CODE_INVALID_AUTH_CODE;
import static info.zelazko.minibank.util.ErrorMessages.ERROR_CODE_TRANSFER_NOT_FOUND;

public class ConfirmTransferProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of("MissingTransfer",        // scenario title
                        ERROR_CODE_TRANSFER_NOT_FOUND, // expected code
                        UUID1,                         // requested uuid
                        Optional.empty(),              // source
                        Optional.empty(),              // destination
                        Optional.empty(),              // transfer
                        null),

                Arguments.of("EmptyPayload",
                        ERROR_CODE_EMPTY_REQUEST,
                        UUID1,
                        Optional.empty(),
                        Optional.empty(),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500),
                        null),

                Arguments.of("InvalidAuthCode",
                        ERROR_CODE_INVALID_AUTH_CODE,
                        UUID1,
                        Optional.empty(),
                        Optional.empty(),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500),
                        new ConfirmCommand(AUTH_CODE_INVALID)),

                Arguments.of("MissingAuthCode",
                        ERROR_CODE_INVALID_AUTH_CODE,
                        UUID1,
                        Optional.empty(),
                        Optional.empty(),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500),
                        new ConfirmCommand(null)),

                Arguments.of("InvalidStatusConfirmed",
                        ERROR_CODE_TRANSFER_STATE_INVALID,
                        UUID1,
                        Optional.empty(),
                        Optional.empty(),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500, Transfer.Status.CONFIRMED),
                        new ConfirmCommand(AUTH_CODE_VALID)),

                Arguments.of("InvalidStatusFailed",
                        ERROR_CODE_TRANSFER_STATE_INVALID,
                        UUID1,
                        Optional.empty(),
                        Optional.empty(),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500, Transfer.Status.FAILED),
                        new ConfirmCommand(AUTH_CODE_VALID)),

                Arguments.of("InvalidAmount",
                        ERROR_CODE_INVALID_AMOUNT,
                        UUID1,
                        Optional.empty(),
                        Optional.empty(),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_0),
                        new ConfirmCommand(AUTH_CODE_VALID)),

                Arguments.of("InsufficientFounds",
                        ERROR_CODE_INVALID_BALANCE,
                        UUID1,
                        prepareEurAccount(IBAN_PL, AMOUNT_500),
                        prepareEurAccount(IBAN_GB, AMOUNT_0),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_1000),
                        new ConfirmCommand(AUTH_CODE_VALID)),

                Arguments.of("SourceAccountCurrencyMismatch",
                        ERROR_CODE_TRANSFER_CURRENCY_SOURCE_MISMATCH,
                        UUID1,
                        preparePlnAccount(IBAN_PL, AMOUNT_1000),
                        prepareEurAccount(IBAN_GB, AMOUNT_0),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500),
                        new ConfirmCommand(AUTH_CODE_VALID)),

                Arguments.of("DestinationAccountCurrencyMismatch",
                        ERROR_CODE_TRANSFER_CURRENCY_DESTINATION_MISMATCH,
                        UUID1,
                        prepareEurAccount(IBAN_PL, AMOUNT_1000),
                        preparePlnAccount(IBAN_GB, AMOUNT_0),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500),
                        new ConfirmCommand(AUTH_CODE_VALID))
        );
    }
}
