package info.zelazko.minibank.util;

import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class MappingHelper {
    private static final String PATH_PARAM_SYNTAX = "(.*):([a-zA-Z]+)";
    private static final String PATH_PARSED = "$1%s";
    private static final int PATH_PARAM_GROUP = 2;

    public static String parse(String path, String value) {
        return path.replaceAll(PATH_PARAM_SYNTAX, String.format(PATH_PARSED, value));
    }

    public static String param(String path) {
        Pattern pattern = Pattern.compile(PATH_PARAM_SYNTAX);
        Matcher matcher = pattern.matcher(path);
        return Optional.of(matcher)
                .filter(Matcher::matches)
                .map(m -> m.group(PATH_PARAM_GROUP))
                .orElse(null);
    }
}
