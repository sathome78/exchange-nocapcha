package me.exrates.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Component
@Scope("session")
public class Payment {

    @NotEmpty
    @Min(10)
    private int userId;

    @NotEmpty
    private int currency;

    @NotEmpty
    @Size(min = 40, max = 60)
    private String meansOfPayment;

    @NotEmpty
    @Range(min = 1)
    private double sum;

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

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
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