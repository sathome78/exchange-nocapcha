package me.exrates.model;

import java.math.BigDecimal;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class CommissionPaymentTuple {

    public final BigDecimal commission;
    public final BigDecimal amount;

    public CommissionPaymentTuple(final BigDecimal commission, final BigDecimal amount) {
        this.commission = commission;
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "CommissionPaymentTuple{" +
                "commission=" + commission +
                ", amount=" + amount +
                '}';
    }
}