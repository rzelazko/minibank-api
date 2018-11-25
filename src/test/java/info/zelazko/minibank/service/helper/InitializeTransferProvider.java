package info.zelazko.minibank.service.helper;

import info.zelazko.minibank.controller.request.InitializeCommand;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Optional;
import java.util.stream.Stream;

import static info.zelazko.minibank.util.ErrorMessages.*;

public class InitializeTransferProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of("EmptyPayload",      // scenario title
                        ERROR_CODE_EMPTY_REQUEST, // expected code
                        Optional.empty(),         // source
                        Optional.empty(),         // destination
                        null),                    // initializeCommand

                Arguments.of("InvalidSourceIban", ERROR_CODE_INVALID_IBAN, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_INVALID)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("MissingSourceIban", ERROR_CODE_INVALID_IBAN, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("InvalidDestinationIban", ERROR_CODE_INVALID_IBAN, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_INVALID)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("MissingDestinationIban", ERROR_CODE_INVALID_IBAN, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("InvalidCurrency", ERROR_CODE_INVALID_CURRENCY, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_INVALID)
                                .build()),

                Arguments.of("MissingCurrency", ERROR_CODE_INVALID_CURRENCY, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .build()),

                Arguments.of("InvalidAmount", ERROR_CODE_INVALID_AMOUNT, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_0)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("MinusAmount", ERROR_CODE_INVALID_AMOUNT, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_M100)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("MissingAmount", ERROR_CODE_INVALID_AMOUNT, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("SourceAccountNotFound", ERROR_CODE_ACCOUNT_NOT_FOUND, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("InvalidSourceAccountBalance", ERROR_CODE_INVALID_BALANCE,
                        MockBuilder.prepareEurAccount(MockValue.IBAN_PL, MockValue.AMOUNT_500),
                        Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_1000)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("InvalidSourceAccountCurrency", ERROR_CODE_TRANSFER_CURRENCY_SOURCE_MISMATCH,
                        MockBuilder.prepareEurAccount(MockValue.IBAN_PL, MockValue.AMOUNT_500),
                        Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_PLN)
                                .build()),

                Arguments.of("DestAccountNotFound", ERROR_CODE_ACCOUNT_NOT_FOUND,
                        MockBuilder.prepareEurAccount(MockValue.IBAN_PL, MockValue.AMOUNT_1000),
                        Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("InvalidDestAccountCurrency", ERROR_CODE_TRANSFER_CURRENCY_DESTINATION_MISMATCH,
                        MockBuilder.prepareEurAccount(MockValue.IBAN_PL, MockValue.AMOUNT_1000),
                        MockBuilder.preparePlnAccount(MockValue.IBAN_GB, MockValue.AMOUNT_0),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build())
        );
    }
}
