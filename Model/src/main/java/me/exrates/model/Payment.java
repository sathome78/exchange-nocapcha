package me.exrates.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Component
@Scope("session")
public class Payment {

    private int userId;
    private String targetPayment;
    private String meansOfPayment;
    private Double sum;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTargetPayment() {
        return targetPayment;
    }

    public void setTargetPayment(String targetPayment) {
        this.targetPayment = targetPayment;
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
                "targetPayment='" + targetPayment + '\'' +
                ", meansOfPayment='" + meansOfPayment + '\'' +
                ", sum=" + sum +
                '}';
    }
}