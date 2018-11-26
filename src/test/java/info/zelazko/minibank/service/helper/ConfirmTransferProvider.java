package info.zelazko.minibank.service.helper;

import info.zelazko.minibank.controller.request.ConfirmCommand;
import info.zelazko.minibank.persistance.model.Transfer;
import info.zelazko.minibank.util.MinibankError;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Optional;
import java.util.stream.Stream;

import static info.zelazko.minibank.service.helper.MockBuilder.*;
import static info.zelazko.minibank.service.helper.MockValue.*;

public class ConfirmTransferProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of("MissingTransfer",           // scenario title
                        MinibankError.TRANSFER_NOT_FOUND, // expected code
                        UUID1,                            // requested uuid
                        Optional.empty(),                 // source
                        Optional.empty(),                 // destination
                        Optional.empty(),                 // transfer
                        null),

                Arguments.of("EmptyPayload",
                        MinibankError.EMPTY_REQUEST,
                        UUID1,
                        Optional.empty(),
                        Optional.empty(),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500),
                        null),

                Arguments.of("InvalidAuthCode",
                        MinibankError.INVALID_AUTH_CODE,
                        UUID1,
                        Optional.empty(),
                        Optional.empty(),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500),
                        new ConfirmCommand(AUTH_CODE_INVALID)),

                Arguments.of("MissingAuthCode",
                        MinibankError.INVALID_AUTH_CODE,
                        UUID1,
                        Optional.empty(),
                        Optional.empty(),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500),
                        new ConfirmCommand(null)),

                Arguments.of("InvalidStatusConfirmed",
                        MinibankError.TRANSFER_STATE_INVALID,
                        UUID1,
                        Optional.empty(),
                        Optional.empty(),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500, Transfer.Status.CONFIRMED),
                        new ConfirmCommand(AUTH_CODE_VALID)),

                Arguments.of("InvalidStatusFailed",
                        MinibankError.TRANSFER_STATE_INVALID,
                        UUID1,
                        Optional.empty(),
                        Optional.empty(),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500, Transfer.Status.FAILED),
                        new ConfirmCommand(AUTH_CODE_VALID)),

                Arguments.of("InvalidAmount",
                        MinibankError.INVALID_AMOUNT,
                        UUID1,
                        Optional.empty(),
                        Optional.empty(),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_0),
                        new ConfirmCommand(AUTH_CODE_VALID)),

                Arguments.of("InsufficientFounds",
                        MinibankError.INVALID_BALANCE,
                        UUID1,
                        prepareEurAccount(IBAN_PL, AMOUNT_500),
                        prepareEurAccount(IBAN_GB, AMOUNT_0),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_1000),
                        new ConfirmCommand(AUTH_CODE_VALID)),

                Arguments.of("SourceAccountCurrencyMismatch",
                        MinibankError.TRANSFER_CURRENCY_SOURCE_MISMATCH,
                        UUID1,
                        preparePlnAccount(IBAN_PL, AMOUNT_1000),
                        prepareEurAccount(IBAN_GB, AMOUNT_0),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500),
                        new ConfirmCommand(AUTH_CODE_VALID)),

                Arguments.of("DestinationAccountCurrencyMismatch",
                        MinibankError.TRANSFER_CURRENCY_DESTINATION_MISMATCH,
                        UUID1,
                        prepareEurAccount(IBAN_PL, AMOUNT_1000),
                        preparePlnAccount(IBAN_GB, AMOUNT_0),
                        prepareEurTransfer(IBAN_PL, IBAN_GB, AMOUNT_500),
                        new ConfirmCommand(AUTH_CODE_VALID))
        );
    }
}
