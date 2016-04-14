package me.exrates.model.dto;

/**
 * Created by Valk on 12.04.16.
 */
public class CurrencyPairStatisticsDto {
    private String name;
    private String currency1;
    private String currency2;
    private String lastOrderCurrency;
    private String amountBuy;
    private String sumAmountBuyClosed;
    private String sumAmountSellClosed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmountBuy() {
        return amountBuy;
    }

    public void setAmountBuy(String amountBuy) {
        this.amountBuy = amountBuy;
    }

    public String getSumAmountBuyClosed() {
        return sumAmountBuyClosed;
    }

    public void setSumAmountBuyClosed(String sumAmountBuyClosed) {
        this.sumAmountBuyClosed = sumAmountBuyClosed;
    }

    public String getSumAmountSellClosed() {
        return sumAmountSellClosed;
    }

    public void setSumAmountSellClosed(String sumAmountSellClosed) {
        this.sumAmountSellClosed = sumAmountSellClosed;
    }

    public String getLastOrderCurrency() {
        return lastOrderCurrency;
    }

    public void setLastOrderCurrency(String lastOrderCurrency) {
        this.lastOrderCurrency = lastOrderCurrency;
    }

    public String getCurrency1() {
        return currency1;
    }

    public void setCurrency1(String currency1) {
        this.currency1 = currency1;
    }

    public String getCurrency2() {
        return currency2;
    }

    public void setCurrency2(String currency2) {
        this.currency2 = currency2;
    }
}
