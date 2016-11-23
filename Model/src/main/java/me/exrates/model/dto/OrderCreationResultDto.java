package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

/**
 * Created by OLEG on 21.11.2016.
 */
public class OrderCreationResultDto {
    private Integer createdOrderId;
    private Integer autoAcceptedQuantity;
    private BigDecimal partiallyAcceptedAmount;
    private BigDecimal partiallyAcceptedOrderFullAmount;

    public Integer getCreatedOrderId() {
        return createdOrderId;
    }

    public void setCreatedOrderId(Integer createdOrderId) {
        this.createdOrderId = createdOrderId;
    }

    public Integer getAutoAcceptedQuantity() {
        return autoAcceptedQuantity;
    }

    public void setAutoAcceptedQuantity(Integer autoAcceptedQuantity) {
        this.autoAcceptedQuantity = autoAcceptedQuantity;
    }

    public BigDecimal getPartiallyAcceptedAmount() {
        return partiallyAcceptedAmount;
    }

    public void setPartiallyAcceptedAmount(BigDecimal partiallyAcceptedAmount) {
        this.partiallyAcceptedAmount = partiallyAcceptedAmount;
    }

    public BigDecimal getPartiallyAcceptedOrderFullAmount() {
        return partiallyAcceptedOrderFullAmount;
    }

    public void setPartiallyAcceptedOrderFullAmount(BigDecimal partiallyAcceptedOrderFullAmount) {
        this.partiallyAcceptedOrderFullAmount = partiallyAcceptedOrderFullAmount;
    }

    @Override
    public String toString() {
        return "OrderCreationResultDto{" +
                "createdOrderId=" + createdOrderId +
                ", autoAcceptedQuantity=" + autoAcceptedQuantity +
                ", partiallyAcceptedAmount=" + partiallyAcceptedAmount +
                ", partiallyAcceptedOrderFullAmount=" + partiallyAcceptedOrderFullAmount +
                '}';
    }
}
