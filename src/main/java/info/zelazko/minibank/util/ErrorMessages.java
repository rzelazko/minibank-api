package info.zelazko.minibank.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {
    public static final String ERROR_CODE_DEFAULT = "E000";
    public static final String ERROR_MSG_DEFAULT = "It shouldn't have happened";

    public static final String ERROR_CODE_NOT_FOUND = "E0N1";
    public static final String ERROR_MSG_NOT_FOUND = "Requested resource has not been found";

    public static final String ERROR_CODE_ACCOUNT_NOT_FOUND = "E0N2";
    public static final String ERROR_MSG_ACCOUNT_NOT_FOUND = "Requested account (%s) has not been found";

    public static final String ERROR_CODE_TRANSFER_NOT_FOUND = "E0N3";
    public static final String ERROR_MSG_TRANSFER_NOT_FOUND = "Requested transfer has not been found";

    public static final String ERROR_CODE_INVALID_JSON = "E0P1";
    public static final String ERROR_MSG_INVALID_JSON = "Invalid JSON or invalid request format";

    public static final String ERROR_CODE_INVALID_IBAN = "E0P2";
    public static final String ERROR_MSG_INVALID_IBAN = "Given IBAN (%s) is not valid";

    public static final String ERROR_CODE_INVALID_CURRENCY = "E0P3";
    public static final String ERROR_MSG_INVALID_CURRENCY = "Account currency (%s) is not valid";

    public static final String ERROR_CODE_INVALID_AMOUNT = "E0P4";
    public static final String ERROR_MSG_INVALID_AMOUNT = "Requested amount is not valid";

    public static final String ERROR_CODE_EMPTY_REQUEST = "E0P5";
    public static final String ERROR_MSG_EMPTY_REQUEST = "Error - request is empty";

    public static final String ERROR_CODE_ACCOUNT_EXISTS = "E0A1";
    public static final String ERROR_MSG_ACCOUNT_EXISTS = "Account with IBAN %s already exists";

    public static final String ERROR_CODE_INVALID_BALANCE = "E0T1";
    public static final String ERROR_MSG_INVALID_BALANCE = "Invalid account balance";

    public static final String ERROR_CODE_INVALID_AUTH_CODE = "E0T2";
    public static final String ERROR_MSG_INVALID_AUTH_CODE = "Transfer authorization failed - invalid auth code";

    public static final String ERROR_CODE_TRANSFER_CURRENCY_SOURCE_MISMATCH = "E0T3";
    public static final String ERROR_CODE_TRANSFER_CURRENCY_DESTINATION_MISMATCH = "E0T4";
    public static final String ERROR_MSG_TRANSFER_CURRENCY_INVALID = "Transfer currency is invalid";

    public static final String ERROR_CODE_TRANSFER_STATE_INVALID = "E0T5";
    public static final String ERROR_MSG_TRANSFER_STATE_INVALID = "Transfer state is invalid - already confirmed or failed";

    public static final String ERROR_CODE_DEL_NONINITIALIZED = "E0T6";
    public static final String ERROR_MSG_DEL_NONINITIALIZED = "Can not delete transfer with status other than INITIALIZED";


}
