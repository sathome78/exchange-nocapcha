package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * Created by ogolv on 30.07.2016.
 */
public class OrderBasicInfoDto {

    private int id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateCreation;
    private String currencyPairName;
    private String orderTypeName;
    private String exrate;
    private String amountBase;
    private String orderCreatorEmail;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getCurrencyPairName() {
        return currencyPairName;
    }

    public void setCurrencyPairName(String currencyPairName) {
        this.currencyPairName = currencyPairName;
    }

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
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

    public String getOrderCreatorEmail() {
        return orderCreatorEmail;
    }

    public void setOrderCreatorEmail(String orderCreatorEmail) {
        this.orderCreatorEmail = orderCreatorEmail;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderBasicInfoDto{" +
                "id=" + id +
                ", dateCreation=" + dateCreation +
                ", currencyPairName='" + currencyPairName + '\'' +
                ", orderTypeName='" + orderTypeName + '\'' +
                ", exrate='" + exrate + '\'' +
                ", amountBase='" + amountBase + '\'' +
                ", orderCreatorEmail='" + orderCreatorEmail + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
