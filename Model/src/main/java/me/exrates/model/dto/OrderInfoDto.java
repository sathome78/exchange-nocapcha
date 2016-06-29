package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by Valk on 11.05.2016.
 */
public class OrderInfoDto {
    private int id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateCreation;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateAcception;
    private String currencyPairName;
    private String orderTypeName;
    private String orderStatusName;
    private String exrate;
    private String amountBase;
    private String amountConvert;
    private String currencyBaseName;
    private String currencyConvertName;
    private String orderCreatorEmail;
    private String orderAcceptorEmail;
    private String transactionCount;
    private String companyCommission;

    /*getters setters*/

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

    public LocalDateTime getDateAcception() {
        return dateAcception;
    }

    public void setDateAcception(LocalDateTime dateAcception) {
        this.dateAcception = dateAcception;
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

    public String getOrderStatusName() {
        return orderStatusName;
    }

    public void setOrderStatusName(String orderStatusName) {
        this.orderStatusName = orderStatusName;
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

    public String getCurrencyBaseName() {
        return currencyBaseName;
    }

    public void setCurrencyBaseName(String currencyBaseName) {
        this.currencyBaseName = currencyBaseName;
    }

    public String getCurrencyConvertName() {
        return currencyConvertName;
    }

    public void setCurrencyConvertName(String currencyConvertName) {
        this.currencyConvertName = currencyConvertName;
    }

    public String getOrderCreatorEmail() {
        return orderCreatorEmail;
    }

    public void setOrderCreatorEmail(String orderCreatorEmail) {
        this.orderCreatorEmail = orderCreatorEmail;
    }

    public String getOrderAcceptorEmail() {
        return orderAcceptorEmail;
    }

    public void setOrderAcceptorEmail(String orderAcceptorEmail) {
        this.orderAcceptorEmail = orderAcceptorEmail;
    }

    public String getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(String transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getCompanyCommission() {
        return companyCommission;
    }

    public void setCompanyCommission(String companyCommission) {
        this.companyCommission = companyCommission;
    }
};

