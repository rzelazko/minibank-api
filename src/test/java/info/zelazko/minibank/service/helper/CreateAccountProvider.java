package info.zelazko.minibank.service.helper;

import info.zelazko.minibank.controller.request.AccountPayload;
import info.zelazko.minibank.persistance.model.Account;
import info.zelazko.minibank.util.MinibankError;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Optional;
import java.util.stream.Stream;

import static info.zelazko.minibank.service.helper.MockValue.*;

public class CreateAccountProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of("EmptyPayload",         // scenario title
                        MinibankError.EMPTY_REQUEST, // expected code
                        Optional.empty(),            // account
                        null),                       // accountPayload

                Arguments.of("createAccountInvalidIban",
                        MinibankError.INVALID_IBAN,
                        Optional.empty(),
                        AccountPayload.builder()
                                .iban(IBAN_INVALID)
                                .currency(CURRENCY_PLN.getCurrencyCode())
                                .balance(AMOUNT_1000)
                                .build()),

                Arguments.of("createAccountMissingIban",
                        MinibankError.INVALID_IBAN,
                        Optional.empty(),
                        AccountPayload.builder()
                                .currency(CURRENCY_PLN.getCurrencyCode())
                                .balance(AMOUNT_1000)
                                .build()),

                Arguments.of("createAccountDuplicatedIban",
                        MinibankError.ACCOUNT_EXISTS,
                        Optional.of(Account.builder()
                                .iban(IBAN_PL)
                                .currency(CURRENCY_PLN)
                                .balance(AMOUNT_1000)
                                .build()),
                        AccountPayload.builder()
                                .iban(IBAN_PL)
                                .currency(CURRENCY_PLN.getCurrencyCode())
                                .balance(AMOUNT_1000)
                                .build()),

                Arguments.of("createAccountInvalidCurrency",
                        MinibankError.INVALID_CURRENCY,
                        Optional.empty(),
                        AccountPayload.builder()
                                .iban(IBAN_PL)
                                .currency(CURRENCY_CODE_INVALID)
                                .balance(AMOUNT_1000)
                                .build()),

                Arguments.of("createAccountMissingCurrency",
                        MinibankError.INVALID_CURRENCY,
                        Optional.empty(),
                        AccountPayload.builder()
                                .iban(IBAN_PL)
                                .balance(AMOUNT_1000)
                                .build())
        );
    }
}
