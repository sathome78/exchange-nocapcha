package me.exrates.model.enums;

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
}
