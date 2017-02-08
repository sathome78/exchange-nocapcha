package me.exrates.model.vo;

/**
 * Created by OLEG on 07.02.2017.
 */
public class InvoiceConfirmData {
    private Integer invoiceId;
    private String payeeBankName;
    private String userAccount;
    private String userFullName;
    private String remark;

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPayeeBankName() {
        return payeeBankName;
    }

    public void setPayeeBankName(String payeeBankName) {
        this.payeeBankName = payeeBankName;
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
                "invoiceId=" + invoiceId +
                ", payeeBankName='" + payeeBankName + '\'' +
                ", userAccount='" + userAccount + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
