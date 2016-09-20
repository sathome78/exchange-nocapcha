package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.ExOrder;
import me.exrates.model.Merchant;
import me.exrates.model.enums.OperationType;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OperationViewDto {

    private BigDecimal amount;
    private BigDecimal amountBuy;
    private BigDecimal commissionAmount;
    private String operationType;
    private String currency;
    private Merchant merchant;
	@JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime datetime;
    private ExOrder order;
	private String status;

	public ExOrder getOrder() {
		return order;
	}
	public void setOrder(ExOrder order) {
		this.order = order;
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
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "OperationViewDto{" +
				"amount=" + amount +
				", amountBuy=" + amountBuy +
				", commissionAmount=" + commissionAmount +
				", operationType=" + operationType +
				", currency='" + currency + '\'' +
				", merchant=" + merchant +
				", datetime=" + datetime +
				", order=" + order +
				", status='" + status + '\'' +
				'}';
	}
}
