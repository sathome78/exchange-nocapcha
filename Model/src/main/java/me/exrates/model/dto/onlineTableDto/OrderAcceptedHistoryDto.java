package me.exrates.model.dto.onlineTableDto;

import me.exrates.model.dto.onlineTableDto.OnlineTableDto;
import me.exrates.model.enums.OperationType;

/**
 * Created by Valk on 03.06.2016.
 */
public class OrderAcceptedHistoryDto extends OnlineTableDto {
    private Integer orderId;
    private String dateAcceptionTime;
    private String rate;
    private String amountBase;
    private OperationType operationType;

    public OrderAcceptedHistoryDto() {
        this.needRefresh = true;
    }

    public OrderAcceptedHistoryDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    /*hash*/
    @Override
    public int hashCode() {
        return orderId != null ? orderId.hashCode() : 0;
    }
   /*getters setters*/

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public String getDateAcceptionTime() {
        return dateAcceptionTime;
    }

    public void setDateAcceptionTime(String dateAcceptionTime) {
        this.dateAcceptionTime = dateAcceptionTime;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAmountBase() {
        return amountBase;
    }

    public void setAmountBase(String amountBase) {
        this.amountBase = amountBase;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }
}
