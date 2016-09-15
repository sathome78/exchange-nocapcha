package me.exrates.model.dto;

import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

/**
 * Created by ajet on 14.09.2016.
 * <p/>
 * class is used for upload data
 */
public class UserSummaryTotalInOutDto {
    private String currency;
    private BigDecimal totalIn;
    private BigDecimal totalOut;

    public static String getTitle() {
        return "Currency" + ";" +
                "In" + ";" +
                "Out" +
                "\r\n";
    }

    @Override
    public String toString() {
        return currency + ";" +
                BigDecimalProcessing.formatNoneComma(totalIn, false) + ";" +
                BigDecimalProcessing.formatNoneComma(totalOut, false) +
                "\r\n";
    }

    /*getters setters*/

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTotalIn() {
        return totalIn;
    }

    public void setTotalIn(BigDecimal totalIn) {
        this.totalIn = totalIn;
    }

    public BigDecimal getTotalOut() {
        return totalOut;
    }

    public void setTotalOut(BigDecimal totalOut) {
        this.totalOut = totalOut;
    }
}
