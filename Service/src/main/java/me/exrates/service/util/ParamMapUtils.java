package me.exrates.service.util;

import java.util.Map;
import java.util.Objects;

public class ParamMapUtils {

    private ParamMapUtils() {
    }

    public static <T> T getIfNotNull(Map<String, T> params, String paramName) {
        T value = params.get(paramName);
        Objects.requireNonNull(value, String.format("Absent value for param %s", paramName));
        return value;
    }
}
