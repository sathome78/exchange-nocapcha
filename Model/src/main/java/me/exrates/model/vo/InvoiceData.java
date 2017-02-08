package me.exrates.model.vo;

import me.exrates.model.CreditsOperation;

/**
 * Created by OLEG on 03.02.2017.
 */
public class InvoiceData {

    private CreditsOperation creditsOperation;
    private Integer bankId;
    private String userFullName;
    private String remark;

    public CreditsOperation getCreditsOperation() {
        return creditsOperation;
    }

    public void setCreditsOperation(CreditsOperation creditsOperation) {
        this.creditsOperation = creditsOperation;
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
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
        return "InvoiceData{" +
                "creditsOperation=" + creditsOperation +
                ", bankId=" + bankId +
                ", userFullName='" + userFullName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}

