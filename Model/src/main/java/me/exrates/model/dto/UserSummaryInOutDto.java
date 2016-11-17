package me.exrates.model.dto;

import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

/**
 * Created by ajet on 14.09.2016.
 * <p/>
 * class is used for upload data
 */
public class UserSummaryInOutDto {
    private String userNickname;
    private String userEmail;
    private String creationIn;
    private String creationOut;
    private String confirmationOut;
    private String merchantName;
    private String currencyName;
    private BigDecimal amount;

    public static String getTitle() {
        return "Name" + ";" +
                "Email" + ";" +
                "Creation In" + ";" +
                "Creation Out" + ";" +
                "Confirmation Out" + ";" +
                "Merchant" + ";" +
                "Wallet" + ";" +
                "Sum" +
                "\r\n";
    }

    @Override
    public String toString() {
        return userNickname + ";" +
                userEmail + ";" +
                creationIn + ";" +
                creationOut + ";" +
                confirmationOut + ";" +
                (merchantName == null ? "" : merchantName) + ";" +
                (currencyName == null ? "" : currencyName) + ";" +
                BigDecimalProcessing.formatNoneComma(amount, false) +
                "\r\n";
    }

    /*getters setters*/

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCreationIn() {
        return creationIn;
    }

    public void setCreationIn(String creationIn) {
        this.creationIn = creationIn;
    }

    public String getCreationOut() {
        return creationOut;
    }

    public void setCreationOut(String creationOut) {
        this.creationOut = creationOut;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getConfirmationOut() {
        return confirmationOut;
    }

    public void setConfirmationOut(String confirmationOut) {
        this.confirmationOut = confirmationOut;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}
