package me.exrates.model.dto;

import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

/**
 * Created by Valk on 04.05.2016.
 * <p/>
 * class is used for upload data
 */
public class UsersWalletsDto {
    private String userNickname;
    private String userEmail;
    private String currencyName;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;

    @Override
    public String toString() {
        return userNickname + ";" +
                userEmail + ";" +
                currencyName + ";" +
                BigDecimalProcessing.formatNoneComma(activeBalance) + ";" +
                BigDecimalProcessing.formatNoneComma(reservedBalance) +
                "\r\n";
    }

    /*getters setters*/
    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public BigDecimal getActiveBalance() {
        return BigDecimalProcessing.normalize(activeBalance);
    }

    public void setActiveBalance(BigDecimal activeBalance) {
        this.activeBalance = activeBalance;
    }

    public BigDecimal getReservedBalance() {
        return BigDecimalProcessing.normalize(reservedBalance);
    }

    public void setReservedBalance(BigDecimal reservedBalance) {
        this.reservedBalance = reservedBalance;
    }

}
