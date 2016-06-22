package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedIntervalTypeException;

/**
 * Created by Valk on 27.04.2016.
 */
public enum IntervalType {
    HOUR,
    DAY,
    YEAR,
    MONTH;

    public static IntervalType convert(String str) {
        switch (str) {
            case "HOUR":
                return HOUR;
            case "DAY":
                return DAY;
            case "MONTH":
                return MONTH;
            case "YEAR":
                return YEAR;
            default:
                throw new UnsupportedIntervalTypeException(str);
        }
    }
}
