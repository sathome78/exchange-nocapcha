package me.exrates.model.enums.invoice;

import lombok.ToString;

/**
 * Created by OLEG on 28.02.2017.
 */
@ToString
public enum InvoiceOperationDirection {

    REFILL, WITHDRAW, TRANSFER_VOUCHER;

    @Override
    public String toString() {
        return "InvoiceOperationDirection " + this.name();
    }
}
