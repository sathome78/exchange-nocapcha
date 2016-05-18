package me.exrates.model.util;

import me.exrates.model.enums.ActionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Contained constans and methods to operate with BigDecimal value
 * Created by Valk on 30.04.2016.
 */

public class BigDecimalProcessing {
    protected static final int SCALE = 9;
    protected static final RoundingMode ROUND_TYPE = RoundingMode.HALF_UP;
    protected static final String PATTERN = "###,##0." + new String(new char[SCALE]).replace("\0", "0");
    protected static final String PATTERN_SHORT = "###,##0." + new String(new char[SCALE]).replace("\0", "#");


    /**
     * Checks operands and if operand is "null" sets it to 0
     * @param value1
     * @param value2
     * @param actionType
     * @return result of doAction with the same arguments
     */
    public static BigDecimal doActionLax(BigDecimal value1, BigDecimal value2, ActionType actionType) {
        if (value1 == null) value1 = BigDecimal.ZERO;
        if (value2 == null) value2 = BigDecimal.ZERO;
        return doAction(value1, value2, actionType);
    }

    /**
     * Executes arithmetic operation and returns BigDecimal value with applied <b>SCALE</b> and <b>ROUND_TYPE</b>
     * and removed trailing zeros. Or "null" if at least one of operands is "null"
     * Before execution operation to operands apply <b>SCALE</b> and <b>ROUND_TYPE</b>
     * @param value1 is the first operand for operation
     * @param value2 is the second operand for operation
     * @param actionType
     * @return BigDecimal value with applied <b>SCALE</b> and <b>ROUND_TYPE</b>
     * and removed trailing zeros. Or "null" if at least one of operands is "null"
     */
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
            case SUBTRACT: {
                result = value1.subtract(value2);
                break;
            }
            case MULTIPLY: {
                result = value1.multiply(value2);
                break;
            }
            /*calculate value2 percent from value1*/
            case MULTIPLY_PERCENT: {
                result = value1.multiply(value2).divide(new BigDecimal(100), ROUND_TYPE);
                break;
            }
            /*calculate the growth in percent value2 relative value1
            * 50, 120 -> 120/50*100-100 -> 140*/
            case PERCENT_GROWTH: {
                result = value2.divide(value1, ROUND_TYPE).multiply(BigDecimal.valueOf(100)).add(BigDecimal.valueOf(100).negate());
                break;
            }
            case DEVIDE: {
                result = value1.divide(value2, ROUND_TYPE);
                break;
            }
        }
        return normalize(result);
    }

    /**
     * Removes trailing zeros in BigDecimal value
     *
     * @param bigDecimal
     * @return BigDecimal value without trailing zeros
     */
    public static BigDecimal normalize(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.setScale(SCALE, ROUND_TYPE).stripTrailingZeros().add(BigDecimal.ZERO);
    }

    /**
     * Returns BigDecimal value converted to string
     * with <b>Space</b> as group separator and <b>Comma</b> as decimal separator
     * with trailing zeros if trailingZeros is "true" or without if "false"
     *
     * @param bigDecimal value to convert
     * @return string ov value or "0" if value is null
     * 67553.116000000 => 67 553,116 or 67 553,116000000 (depending on trailingZeros)
     */
    public static String formatSpaceComma(BigDecimal bigDecimal, boolean trailingZeros) {
        DecimalFormat df = new DecimalFormat(trailingZeros ? PATTERN : PATTERN_SHORT);
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        df.setRoundingMode(ROUND_TYPE);
        dfs.setGroupingSeparator(' ');
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    /**
     * Returns BigDecimal value converted to string
     * with <b>No</b> group separator and <b>Comma</b> as decimal separator
     * with trailing zeros if trailingZeros is "true" or without if "false"
     *
     * @param bigDecimal value to convert
     * @return string ov value or "0" if value is null
     * 67553.116000000 => 67553,116 or 67553,116000000 (depending on trailingZeros)
     */
    public static String formatNoneComma(BigDecimal bigDecimal, boolean trailingZeros) {
        DecimalFormat df = new DecimalFormat(trailingZeros ? PATTERN : PATTERN_SHORT);
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        df.setRoundingMode(ROUND_TYPE);
        df.setGroupingUsed(false);
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    /**
     * Returns BigDecimal value converted to string
     * with <b>No</b> group separator and <b>Point</b> as decimal separator
     * with trailing zeros if trailingZeros is "true" or without if "false"
     *
     * @param bigDecimal value to convert
     * @return string ov value or "0" if value is null
     * 67553.116000000 => 67553.116 or 67553.116000000 (depending on trailingZeros)
     */
    public static String formatNonePoint(BigDecimal bigDecimal, boolean trailingZeros) {
        DecimalFormat df = new DecimalFormat(trailingZeros ? PATTERN : PATTERN_SHORT);
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        df.setRoundingMode(ROUND_TYPE);
        df.setGroupingUsed(false);
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    /**
     * Returns BigDecimal value converted to string by formatNonePoint method
     * but result is quoted
     *
     * @param bigDecimal value to convert
     * @return string ov value or "0" if value is null
     * 67553.116000000 => "67553.116" or "67553.116000000" (depending on trailingZeros)
     */
    public static String formatNonePointQuoted(BigDecimal bigDecimal, boolean trailingZeros) {
        return toQuoted(formatNonePoint(bigDecimal, trailingZeros));
    }

    /**
     * Returns BigDecimal value converted to string with group and decimal separators according to locale
     * without trailing zeros
     *
     * @param bigDecimal value to convert
     * @param locale to convert format
     * @return string ov value or "0" if value is null
     * - ru: 67553.116000000 => 67 553,116 or 67 553,116000000 (depending on trailingZeros)
     * - en: 67553.116000000 => 67,553.116 or 67,553.116000000 (depending on trailingZeros)
     */
    public static String formatLocale(BigDecimal bigDecimal, Locale locale, boolean trailingZero) {
        DecimalFormat df = new DecimalFormat(trailingZero ? PATTERN : PATTERN_SHORT);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    /**
     * Simply adds quotes to string value
     * with trailing zeros
     *
     * @param bigDecimalString strin to quated
     * @return 123456.123 => "123456.123"
     */
    public static String toQuoted(String bigDecimalString) {
        return '"' + bigDecimalString + '"';
    }

}
