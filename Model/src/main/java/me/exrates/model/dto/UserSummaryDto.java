package me.exrates.model.dto;

import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

/**
 * Created by Valk on 04.05.2016.
 * <p/>
 * class is used for upload data
 */
public class UserSummaryDto {
    private String userNickname;
    private String userEmail;
    private String creationDate;
    private String registeredIp;
    private String lastIp;
    private String currencyName;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;
    private BigDecimal walletTurnover;

    public static String getTitle() {
        return "Name" + ";" +
                "Email" + ";" +
                "Creation date" + ";" +
                "IP" + ";" +
                "Last IP" + ";" +
                "Wallet" + ";" +
                "balance" + ";" +
                "reserve" + ";" +
                "return" +
                "\r\n";
    }

    @Override
    public String toString() {
        return userNickname + ";" +
                userEmail + ";" +
                creationDate + ";" +
                (registeredIp == null ? "" : registeredIp) + ";" +
                (lastIp == null ? "" : lastIp) + ";" +
                (currencyName == null ? "" : currencyName) + ";" +
                BigDecimalProcessing.formatNoneComma(activeBalance, false) + ";" +
                BigDecimalProcessing.formatNoneComma(reservedBalance, false) + ";" +
                BigDecimalProcessing.formatNoneComma(walletTurnover, false) +
                "\r\n";
    }

    /*getters setters*/

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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getRegisteredIp() {
        return registeredIp;
    }

    public void setRegisteredIp(String registeredIp) {
        this.registeredIp = registeredIp;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public BigDecimal getWalletTurnover() {
        return walletTurnover;
    }

    public void setWalletTurnover(BigDecimal walletTurnover) {
        this.walletTurnover = walletTurnover;
    }

    public BigDecimal getActiveBalance() {
        return activeBalance;
    }

    public void setActiveBalance(BigDecimal activeBalance) {
        this.activeBalance = activeBalance;
    }

    public BigDecimal getReservedBalance() {
        return reservedBalance;
    }

    public void setReservedBalance(BigDecimal reservedBalance) {
        this.reservedBalance = reservedBalance;
    }
}
