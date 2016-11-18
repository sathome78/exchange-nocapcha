package me.exrates.model.dto;

import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

/**
 * Created by ajet on 17.11.2016.
 * <p/>
 * class is used for upload data
 */
public class UserSummaryOrdersDto {
    private String userEmail;
    private String wallet;
    private String role;
    private BigDecimal amountBuy;
    private BigDecimal amountSell;

    public static String getTitle() {
        return  "email" + ";" +
                "wallet" + ";" +
                "group" + ";" +
                "buy" + ";" +
                "sell" +
                "\r\n";
    }

    @Override
    public String toString() {
        return  userEmail + ";" +
                wallet + ";" +
                role + ";" +
                BigDecimalProcessing.formatNoneComma(amountBuy, false) + ";" +
                BigDecimalProcessing.formatNoneComma(amountSell, false) +
                "\r\n";
    }

    /*getters setters*/

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BigDecimal getAmountBuy() {
        return amountBuy;
    }

    public void setAmountBuy(BigDecimal amountBuy) {
        this.amountBuy = amountBuy;
    }

    public BigDecimal getAmountSell() {
        return amountSell;
    }

    public void setAmountSell(BigDecimal amountSell) {
        this.amountSell = amountSell;
    }
}
