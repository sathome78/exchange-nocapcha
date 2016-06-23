package me.exrates.model.dto.onlineTableDto;

import me.exrates.model.dto.onlineTableDto.OnlineTableDto;
import me.exrates.model.enums.OperationType;

/**
 * Created by Valk on 14.04.16.
 */
public class OrderListDto extends OnlineTableDto {
    private int id;
    private int userId;
    private OperationType orderType;
    private String exrate;
    private String amountBase;
    private String amountConvert;

    public OrderListDto() {
        this.needRefresh = true;
    }

    public OrderListDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    /*hash*/

    @Override
    public int hashCode() {
        return id;
    }
    /*getters setters*/

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public OperationType getOrderType() {
        return orderType;
    }

    public void setOrderType(OperationType orderType) {
        this.orderType = orderType;
    }

    public String getExrate() {
        return exrate;
    }

    public void setExrate(String exrate) {
        this.exrate = exrate;
    }

    public String getAmountBase() {
        return amountBase;
    }

    public void setAmountBase(String amountBase) {
        this.amountBase = amountBase;
    }

    public String getAmountConvert() {
        return amountConvert;
    }

    public void setAmountConvert(String amountConvert) {
        this.amountConvert = amountConvert;
    }
}
