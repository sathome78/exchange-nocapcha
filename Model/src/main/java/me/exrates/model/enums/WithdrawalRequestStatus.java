package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedOrderStatusException;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public enum WithdrawalRequestStatus {

    NEW(1),
    ACCEPTED(2),
    DECLINED(3);

    public final int type;

    WithdrawalRequestStatus(final int type) {
        this.type = type;
    }

    public static WithdrawalRequestStatus convert(int id) {
        switch (id) {
            case 1:
                return NEW;
            case 2:
                return ACCEPTED;
            case 3:
                return DECLINED;
            default:
                throw new UnsupportedOrderStatusException(id);
        }
    }

    public int getType() {
        return type;
    }
}
