package info.zelazko.minibank.service.helper;

import info.zelazko.minibank.controller.request.InitializeCommand;
import info.zelazko.minibank.util.MinibankError;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Optional;
import java.util.stream.Stream;

public class InitializeTransferProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of("EmptyPayload",         // scenario title
                        MinibankError.EMPTY_REQUEST, // expected code
                        Optional.empty(),            // source
                        Optional.empty(),            // destination
                        null),                       // initializeCommand

                Arguments.of("InvalidSourceIban", MinibankError.INVALID_IBAN, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_INVALID)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("MissingSourceIban", MinibankError.INVALID_IBAN, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("InvalidDestinationIban", MinibankError.INVALID_IBAN, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_INVALID)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("MissingDestinationIban", MinibankError.INVALID_IBAN, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("InvalidCurrency", MinibankError.INVALID_CURRENCY, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_INVALID)
                                .build()),

                Arguments.of("MissingCurrency", MinibankError.INVALID_CURRENCY, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .build()),

                Arguments.of("InvalidAmount", MinibankError.INVALID_AMOUNT, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_0)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("MinusAmount", MinibankError.INVALID_AMOUNT, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_M100)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("MissingAmount", MinibankError.INVALID_AMOUNT, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("SourceAccountNotFound", MinibankError.ACCOUNT_NOT_FOUND, Optional.empty(), Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("InvalidSourceAccountBalance", MinibankError.INVALID_BALANCE,
                        MockBuilder.prepareEurAccount(MockValue.IBAN_PL, MockValue.AMOUNT_500),
                        Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_1000)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("InvalidSourceAccountCurrency", MinibankError.TRANSFER_CURRENCY_SOURCE_MISMATCH,
                        MockBuilder.prepareEurAccount(MockValue.IBAN_PL, MockValue.AMOUNT_500),
                        Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_PLN)
                                .build()),

                Arguments.of("DestAccountNotFound", MinibankError.ACCOUNT_NOT_FOUND,
                        MockBuilder.prepareEurAccount(MockValue.IBAN_PL, MockValue.AMOUNT_1000),
                        Optional.empty(),
                        InitializeCommand.builder()
                                .source(MockValue.IBAN_PL)
                                .destination(MockValue.IBAN_GB)
                                .amount(MockValue.AMOUNT_500)
                                .currency(MockValue.CURRENCY_CODE_EUR)
                                .build()),

                Arguments.of("InvalidDestAccountCurrency", MinibankError.TRANSFER_CURRENCY_DESTINATION_MISMATCH,
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
