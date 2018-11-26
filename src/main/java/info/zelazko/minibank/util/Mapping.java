package info.zelazko.minibank.util;

public interface Mapping {
    String PATH_API_ACCOUNTS = "/api/accounts";
    String PATH_API_ACCOUNTS_IBAN = "/api/accounts/:iban";
    String PATH_API_TRANSFERS = "/api/transfers";
    String PATH_API_TRANSFERS_UUID = "/api/transfers/:uuid";
}
