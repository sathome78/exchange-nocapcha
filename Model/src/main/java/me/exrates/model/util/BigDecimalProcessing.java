package me.exrates.model.util;

import me.exrates.model.enums.ActionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by Valk on 30.04.2016.
 */
public class BigDecimalProcessing {
    protected static final int SCALE = 9;
    protected static final RoundingMode ROUND_TYPE = RoundingMode.HALF_UP;
    protected static final String PATTERN = "###,###." + new String(new char[SCALE]).replace("\0", "#");

    /*method executes arithmetic operation and returns normalized BigDecimal*/
    public static BigDecimal doAction(BigDecimal value1, BigDecimal value2, ActionType actionType) {
        if (value1 == null || value2 == null) {
            return null;
        }
        BigDecimal result = value1;
        value1 = value1.setScale(SCALE, ROUND_TYPE);
        value2 = value2.setScale(SCALE, ROUND_TYPE);
        switch (actionType) {
            case ADD: {
                result = value1.add(value2);
                break;
            }
            case MULTIPLY: {
                result = value1.multiply(value2);
                break;
            }
            case MULTIPLY_PERCENT: {
                result = value1.multiply(value2).divide(new BigDecimal(100), ROUND_TYPE);
                break;
            }
            case DEVIDE: {
                result = value1.divide(value2, ROUND_TYPE);
                break;
            }
        }
        return normalize(result);
    }

    /*method removes tail zero in BigDecimal*/
    public static BigDecimal normalize(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.setScale(SCALE, ROUND_TYPE).stripTrailingZeros().add(BigDecimal.ZERO);
    }

    public static String formatSpaceComma(BigDecimal bigDecimal) {
        DecimalFormat df = new DecimalFormat(PATTERN);
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        dfs.setGroupingSeparator(' ');
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    public static String formatNoneComma(BigDecimal bigDecimal) {
        DecimalFormat df = new DecimalFormat(PATTERN);
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        df.setGroupingUsed(false);
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    public static String formatLocale(BigDecimal bigDecimal, Locale locale) {
        DecimalFormat df = new DecimalFormat(PATTERN);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    public static String formatToPlainString(BigDecimal bigDecimal) {
        return bigDecimal == null ? BigDecimal.ZERO.setScale(SCALE).toPlainString() : bigDecimal.setScale(SCALE).toPlainString();
    }

    public static String formatToPlainStringQuotes(BigDecimal bigDecimal) {
        return bigDecimal == null ? '"'+BigDecimal.ZERO.setScale(SCALE).toPlainString()+'"' : '"'+bigDecimal.setScale(SCALE).toPlainString()+'"';
    }

}
