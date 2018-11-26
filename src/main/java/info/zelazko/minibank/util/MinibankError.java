package info.zelazko.minibank.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MinibankError {
    DEFAULT("E000", "It shouldn't have happened"),

    NOT_FOUND("E0N1", "Requested resource has not been found"),
    ACCOUNT_NOT_FOUND("E0N2", "Requested account (%s) has not been found"),
    TRANSFER_NOT_FOUND("E0N3", "Requested transfer has not been found"),

    INVALID_JSON("E0P1", "Invalid JSON or invalid request format"),
    INVALID_IBAN("E0P2", "Given IBAN (%s) is not valid"),
    INVALID_CURRENCY("E0P3", "Account currency (%s) is not valid"),
    INVALID_AMOUNT("E0P4", "Requested amount is not valid"),
    EMPTY_REQUEST("E0P5", "MinibankError - request is empty"),

    ACCOUNT_EXISTS("E0A1", "Account with IBAN %s already exists"),

    INVALID_BALANCE("E0T1", "Invalid account balance"),
    INVALID_AUTH_CODE("E0T2",  "Transfer authorization failed - invalid auth code"),
    TRANSFER_CURRENCY_SOURCE_MISMATCH("E0T3", "Transfer currency is invalid"),
    TRANSFER_CURRENCY_DESTINATION_MISMATCH("E0T4", "Transfer currency is invalid"),
    TRANSFER_STATE_INVALID("E0T5", "Transfer state is invalid - already confirmed or failed"),
    DEL_NONINITIALIZED("E0T6", "Can not delete transfer with status other than INITIALIZED");

    private final String code;
    private final String message;
}
