package me.exrates.model.dto;

import java.math.BigDecimal;

/**
 * Created by Valk on 19.04.16.
 */
public class WalletsForOrderAcceptionDto {
    int currencyBase;
    int currencyConvert;
    int companyWalletCurrencyBase;
    int companyWalletCurrencyConvert;
    int userCreatorInWalletId;
    int userCreatorOutWalletId;
    BigDecimal userCreatorOutWalletReserv;
    int userAcceptorInWalletId;
    int userAcceptorOutWalletId;
    BigDecimal userAcceptorOutWalletReserv;

    /*getters setters*/

    public int getCurrencyBase() {
        return currencyBase;
    }

    public void setCurrencyBase(int currencyBase) {
        this.currencyBase = currencyBase;
    }

    public int getCurrencyConvert() {
        return currencyConvert;
    }

    public void setCurrencyConvert(int currencyConvert) {
        this.currencyConvert = currencyConvert;
    }

    public int getCompanyWalletCurrencyBase() {
        return companyWalletCurrencyBase;
    }

    public void setCompanyWalletCurrencyBase(int companyWalletCurrencyBase) {
        this.companyWalletCurrencyBase = companyWalletCurrencyBase;
    }

    public int getCompanyWalletCurrencyConvert() {
        return companyWalletCurrencyConvert;
    }

    public void setCompanyWalletCurrencyConvert(int companyWalletCurrencyConvert) {
        this.companyWalletCurrencyConvert = companyWalletCurrencyConvert;
    }

    public int getUserCreatorInWalletId() {
        return userCreatorInWalletId;
    }

    public void setUserCreatorInWalletId(int userCreatorInWalletId) {
        this.userCreatorInWalletId = userCreatorInWalletId;
    }

    public int getUserCreatorOutWalletId() {
        return userCreatorOutWalletId;
    }

    public void setUserCreatorOutWalletId(int userCreatorOutWalletId) {
        this.userCreatorOutWalletId = userCreatorOutWalletId;
    }

    public BigDecimal getUserCreatorOutWalletReserv() {
        return userCreatorOutWalletReserv;
    }

    public void setUserCreatorOutWalletReserv(BigDecimal userCreatorOutWalletReserv) {
        this.userCreatorOutWalletReserv = userCreatorOutWalletReserv;
    }

    public int getUserAcceptorInWalletId() {
        return userAcceptorInWalletId;
    }

    public void setUserAcceptorInWalletId(int userAcceptorInWalletId) {
        this.userAcceptorInWalletId = userAcceptorInWalletId;
    }

    public int getUserAcceptorOutWalletId() {
        return userAcceptorOutWalletId;
    }

    public void setUserAcceptorOutWalletId(int userAcceptorOutWalletId) {
        this.userAcceptorOutWalletId = userAcceptorOutWalletId;
    }

    public BigDecimal getUserAcceptorOutWalletReserv() {
        return userAcceptorOutWalletReserv;
    }

    public void setUserAcceptorOutWalletReserv(BigDecimal userAcceptorOutWalletReserv) {
        this.userAcceptorOutWalletReserv = userAcceptorOutWalletReserv;
    }
}
