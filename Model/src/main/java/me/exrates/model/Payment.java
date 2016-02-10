package me.exrates.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Component
@Scope("session")
public class Payment {

    private int userId;
    private int currency;
    private String meansOfPayment;
    private double sum;
    private int merchant;

    public enum TransactionType {

        INPUT(1),
        OUTPUT(0);

        public final int operation;

        TransactionType(int operation) {
            this.operation = operation;
        }

        public int getOperation() {
            return operation;
        }
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public String getMeansOfPayment() {
        return meansOfPayment;
    }

    public void setMeansOfPayment(String meansOfPayment) {
        this.meansOfPayment = meansOfPayment;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public int getMerchant() {
        return merchant;
    }

    public void setMerchant(int merchant) {
        this.merchant = merchant;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "currency='" + currency+ '\'' +
                ", meansOfPayment='" + meansOfPayment + '\'' +
                ", sum=" + sum +
                '}';
    }
}