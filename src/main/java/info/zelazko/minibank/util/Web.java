package info.zelazko.minibank.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class Web {
    public static final String PATH_API_ACCOUNTS = "/api/accounts";
    public static final String PATH_API_ACCOUNTS_IBAN = "/api/accounts/:iban";
    public static final String PATH_API_TRANSFERS = "/api/transfers";
    public static final String PATH_API_TRANSFERS_UUID = "/api/transfers/:uuid";
    public static final String HEADER_HTTP_LOCATION = "Location";
    public static final String MEDIA_TYPE_JSON = "application/json; charset=utf-8";

    public static class Path {
        private static final String PATH_PARAM_SYNTAX = "(.*):([a-zA-Z]+)";
        private static final String PATH_PARSED = "$1%s";
        private static final int PATH_PARAM_GROUP = 2;

        public static String parse(String path, String value) {
            return path.replaceAll(PATH_PARAM_SYNTAX, String.format(PATH_PARSED, value));
        }

        public static String param(String path) {
            Pattern pattern = Pattern.compile(PATH_PARAM_SYNTAX);
            Matcher matcher = pattern.matcher(path);
            if (matcher.matches()) {
                return matcher.group(PATH_PARAM_GROUP);
            }

            return null;
        }
    }

}
