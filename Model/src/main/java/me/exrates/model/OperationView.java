package me.exrates.model;

import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OperationView {

    private BigDecimal amount;
    private BigDecimal amountBuy;
    private BigDecimal commissionAmount;
    private OperationType operationType;
    private String currency;
    private String currencyBuy;
    private Merchant merchant;
    private LocalDateTime datetime;
    private OrderStatus orderStatus;
    
    
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getAmountBuy() {
		return amountBuy;
	}
	public void setAmountBuy(BigDecimal amountBuy) {
		this.amountBuy = amountBuy;
	}
	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}
	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}
	public OperationType getOperationType() {
		return operationType;
	}
	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getCurrencyBuy() {
		return currencyBuy;
	}
	public void setCurrencyBuy(String currencyBuy) {
		this.currencyBuy = currencyBuy;
	}
	public Merchant getMerchant() {
		return merchant;
	}
	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}
	public LocalDateTime getDatetime() {
		return datetime;
	}
	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}
    
    

}
