package me.exrates.model.vo;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * Created by OLEG on 07.02.2017.
 */
public class WithdrawData {
    @NotNull
    private String payerBankName;
    private String payerBankCode;
    @NotNull
    private String userAccount;
    private String userFullName;
    private String remark;


    public String getPayerBankName() {
        return payerBankName;
    }

    public void setPayerBankName(String payerBankName) {
        this.payerBankName = payerBankName;
    }

    public String getPayerBankCode() {
        return payerBankCode;
    }

    public void setPayerBankCode(String payerBankCode) {
        this.payerBankCode = payerBankCode;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    @Override
    public String toString() {
        return "InvoiceConfirmData{" +
                ", payerBankName='" + payerBankName + '\'' +
                ", payerBankCode='" + payerBankCode + '\'' +
                ", userAccount='" + userAccount + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
