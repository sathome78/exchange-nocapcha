package me.exrates.model;

import me.exrates.model.enums.OperationType;

import javax.validation.constraints.Min;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class Payment {

    @Min(value = 30,message = "Bad currency")
    private int currency;
    private int merchant;
    private double sum;
    private String destination;
    private int merchantImage;

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

    public int getMerchantImage() {
        return merchantImage;
    }

    public void setMerchantImage(int merchantImage) {
        this.merchantImage = merchantImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payment payment = (Payment) o;

        if (currency != payment.currency) return false;
        if (merchant != payment.merchant) return false;
        if (Double.compare(payment.sum, sum) != 0) return false;
        if (merchantImage != payment.merchantImage) return false;
        if (destination != null ? !destination.equals(payment.destination) : payment.destination != null) return false;
        return operationType == payment.operationType;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = currency;
        result = 31 * result + merchant;
        temp = Double.doubleToLongBits(sum);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + merchantImage;
        result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "currency=" + currency +
                ", merchant=" + merchant +
                ", sum=" + sum +
                ", destination='" + destination + '\'' +
                ", merchantImage=" + merchantImage +
                ", operationType=" + operationType +
                '}';
    }
}