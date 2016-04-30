package me.exrates.model.util;

import me.exrates.model.enums.ActionType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Created by Valk on 30.04.2016.
 */
public class BigDecimalProcessing {
    protected static final int SCALE = 9;
    protected static final RoundingMode ROUND_TYPE = RoundingMode.HALF_UP;

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
                result = value1.multiply(value2).divide(new BigDecimal(100));
                break;
            }
            case DEVIDE: {
                result = value1.divide(value2);
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
        bigDecimal = bigDecimal.setScale(SCALE, ROUND_TYPE);
        if (SCALE == 0) {
            /*if no "."*/
            return bigDecimal;
        }
        /*"." is always present, so next operation is safe*/
        String trimmedValueString = bigDecimal.toString()
                .replaceAll("0+$", "")
                .replaceAll("\\.", "")
                .replaceAll("^0+", "");
        int precision = trimmedValueString.length();
        return bigDecimal.add(BigDecimal.ZERO, new MathContext(precision, RoundingMode.HALF_UP));
    }
}
