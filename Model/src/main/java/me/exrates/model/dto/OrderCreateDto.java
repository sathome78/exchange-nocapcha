package me.exrates.model.dto;

import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Valk on 13.04.16.
 */

public class OrderCreateDto {
    /*this field filled from existing order*/
    private int orderId;
    private int userId;
    private OrderStatus status;
    /*these fields will be transferred to blank creation form */
    private CurrencyPair currencyPair;
    private int comissionForBuyId;
    private BigDecimal comissionForBuyRate;
    private int comissionForSellId;
    private BigDecimal comissionForSellRate;
    private int walletIdCurrencyBase;
    private BigDecimal currencyBaseBalance;
    private int walletIdCurrencyConvert;
    private BigDecimal currencyConvertBalance;
    //
    /*these fields will be returned from creation form after submitting*/
    private OperationType operationType;
    private BigDecimal exchangeRate;
    private BigDecimal amount; //amount of base currency: base currency can be bought or sold dependending on operationType
    //
    /*
    * these fields will be calculated after submitting the order and before final creation confirmation the order
    * (here: OrderController.submitNewOrderToSell())
    * These amounts calculated directly in java (after check the order parameters in java validator) and will be persistented in db
    * (before this step these amounts were being calculated by javascript and may be occur some difference)
    * */
    private BigDecimal total; //calculated amount of currency conversion = amount * exchangeRate
    private int comissionId;
    private BigDecimal comission; //calculated comission amount depending on operationType and corresponding comission rate
    private BigDecimal totalWithComission; //total + comission

    /*constructors*/

    public OrderCreateDto() {
    }

    /*service methods*/
    public OrderSum getCalculatedAmounts() {
        if (operationType == null) {
            return null;
        }
        OrderSum result = new OrderSum();
        if (operationType == OperationType.BUY) {
            result.total = amount.multiply(exchangeRate);
            result.comissionId = comissionForBuyId;
            result.comission = result.total.multiply(comissionForBuyRate).divide(new BigDecimal(100));
            result.totalWithComission = result.total.add(result.comission);
        } else {
            result.total = amount.multiply(exchangeRate);
            result.comissionId = comissionForSellId;
            result.comission = result.total.multiply(comissionForSellRate).divide(new BigDecimal(100));
            result.totalWithComission = result.total.add(result.comission.negate());
        }
        return result;
    }

    public class OrderSum {
        public BigDecimal total;
        public int comissionId;
        public BigDecimal comission;
        public BigDecimal totalWithComission;
    }

    public String getTrimmedValue(BigDecimal value) {
        String sep = "\\.";
        String result = value.toString().split(sep).length < 2 ? value.toString() : value.toString().split(sep)[0] + "." + value.toString().split(sep)[1].replaceAll("0+$", "");
        return result.replaceAll("\\.$", "");
    }

    ;

    /*getters setters*/

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    public int getComissionForBuyId() {
        return comissionForBuyId;
    }

    public void setComissionForBuyId(int comissionForBuyId) {
        this.comissionForBuyId = comissionForBuyId;
    }

    public BigDecimal getComissionForBuyRate() {
        return comissionForBuyRate;
    }

    public void setComissionForBuyRate(BigDecimal comissionForBuyRate) {
        this.comissionForBuyRate = comissionForBuyRate;
    }

    public int getComissionForSellId() {
        return comissionForSellId;
    }

    public void setComissionForSellId(int comissionForSellId) {
        this.comissionForSellId = comissionForSellId;
    }

    public BigDecimal getComissionForSellRate() {
        return comissionForSellRate;
    }

    public void setComissionForSellRate(BigDecimal comissionForSellRate) {
        this.comissionForSellRate = comissionForSellRate;
    }

    public int getWalletIdCurrencyBase() {
        return walletIdCurrencyBase;
    }

    public void setWalletIdCurrencyBase(int walletIdCurrencyBase) {
        this.walletIdCurrencyBase = walletIdCurrencyBase;
    }

    public BigDecimal getCurrencyBaseBalance() {
        return currencyBaseBalance;
    }

    public void setCurrencyBaseBalance(BigDecimal currencyBaseBalance) {
        this.currencyBaseBalance = currencyBaseBalance;
    }

    public int getWalletIdCurrencyConvert() {
        return walletIdCurrencyConvert;
    }

    public void setWalletIdCurrencyConvert(int walletIdCurrencyConvert) {
        this.walletIdCurrencyConvert = walletIdCurrencyConvert;
    }

    public BigDecimal getCurrencyConvertBalance() {
        return currencyConvertBalance;
    }

    public void setCurrencyConvertBalance(BigDecimal currencyConvertBalance) {
        this.currencyConvertBalance = currencyConvertBalance;
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

    public int getComissionId() {
        return comissionId;
    }

    public void setComissionId(int comissionId) {
        this.comissionId = comissionId;
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
