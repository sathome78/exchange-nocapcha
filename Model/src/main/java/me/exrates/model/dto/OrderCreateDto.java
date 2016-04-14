package me.exrates.model.dto;

import me.exrates.model.CurrencyPair;
import me.exrates.model.enums.OperationType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by Valk on 13.04.16.
 */

@Component
public class OrderCreateDto {
    /*these fields will be transferred to blank creation form */
    private CurrencyPair currencyPair;
    private BigDecimal comissionForBuy;
    private BigDecimal comissionForSell;
    private int walletIdCurrency1;
    private BigDecimal balance1;
    private int walletIdCurrency2;
    private BigDecimal balance2;
    //
    /*these fields will be returned from creation form after submitting*/
    private OperationType operationType;
    private BigDecimal exchangeRate;
    private BigDecimal amount;
    //
    /*these fields will be transferred after submitting and before final creation confirmation the ordder
    * It's necessary for verification of the amounts calculated directly in java and will be persistented in db
    * Before this step this amounts are calculated by javascript and may be occur some difference*/
    BigDecimal total;
    BigDecimal comission;
    BigDecimal totalWithComission;
    //
    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    public BigDecimal getComissionForBuy() {
        return comissionForBuy;
    }

    public void setComissionForBuy(BigDecimal comissionForBuy) {
        this.comissionForBuy = comissionForBuy;
    }

    public BigDecimal getComissionForSell() {
        return comissionForSell;
    }

    public void setComissionForSell(BigDecimal comissionForSell) {
        this.comissionForSell = comissionForSell;
    }

    public int getWalletIdCurrency1() {
        return walletIdCurrency1;
    }

    public void setWalletIdCurrency1(int walletIdCurrency1) {
        this.walletIdCurrency1 = walletIdCurrency1;
    }

    public BigDecimal getBalance1() {
        return balance1;
    }

    public void setBalance1(BigDecimal balance1) {
        this.balance1 = balance1;
    }

    public int getWalletIdCurrency2() {
        return walletIdCurrency2;
    }

    public void setWalletIdCurrency2(int walletIdCurrency2) {
        this.walletIdCurrency2 = walletIdCurrency2;
    }

    public BigDecimal getBalance2() {
        return balance2;
    }

    public void setBalance2(BigDecimal balance2) {
        this.balance2 = balance2;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getComission() {
        return comission;
    }

    public void setComission(BigDecimal comission) {
        this.comission = comission;
    }

    public BigDecimal getTotalWithComission() {
        return totalWithComission;
    }

    public void setTotalWithComission(BigDecimal totalWithComission) {
        this.totalWithComission = totalWithComission;
    }
}
