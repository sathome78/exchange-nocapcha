package me.exrates.model;

/**
 * Created by OLEG on 02.02.2017.
 */
public class InvoiceBank {

    private Integer id;
    private Integer currencyId;
    private String name;
    private String accountNumber;
    private String recipient;

    public InvoiceBank() {
    }

    public InvoiceBank(Integer id, Integer currencyId, String name, String accountNumber, String recipient) {
        this.id = id;
        this.currencyId = currencyId;
        this.name = name;
        this.accountNumber = accountNumber;
        this.recipient = recipient;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public String toString() {
        return "InvoiceBank{" +
                "id=" + id +
                ", currencyId=" + currencyId +
                ", name='" + name + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", recipient='" + recipient + '\'' +
                '}';
    }
}
