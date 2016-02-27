package me.exrates.model;

import me.exrates.model.enums.OperationType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Component
@Scope("session")
public class Payment {

    private int currency;
    private int merchant;
    private double sum;
    private String destination;

    @NotNull
    private OperationType operationType;

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
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

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "currency=" + currency +
                ", merchant=" + merchant +
                ", sum=" + sum +
                ", destination='" + destination + '\'' +
                ", operationType=" + operationType +
                '}';
    }
}