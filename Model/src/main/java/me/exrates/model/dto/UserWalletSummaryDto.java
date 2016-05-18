package me.exrates.model.dto;

import me.exrates.model.Currency;
import me.exrates.model.User;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

/**
 * Created by Valk on 04.05.2016.
 */
public class UserWalletSummaryDto {
    private String currencyName;
    private int walletsAmount;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;
    private BigDecimal activeBalancePerWallet;
    private BigDecimal reservedBalancePerWallet;

    /*getters setters*/

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public int getWalletsAmount() {
        return walletsAmount;
    }

    public void setWalletsAmount(int walletsAmount) {
        this.walletsAmount = walletsAmount;
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

    public BigDecimal getActiveBalancePerWallet() {
        return activeBalancePerWallet;
    }

    public void setActiveBalancePerWallet(BigDecimal activeBalancePerWallet) {
        this.activeBalancePerWallet = activeBalancePerWallet;
    }

    public BigDecimal getReservedBalancePerWallet() {
        return reservedBalancePerWallet;
    }

    public void setReservedBalancePerWallet(BigDecimal reservedBalancePerWallet) {
        this.reservedBalancePerWallet = reservedBalancePerWallet;
    }
}
