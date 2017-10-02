package me.exrates.model.enums;

/**
 * Created by Maks on 29.09.2017.
 */
public enum NotificationMessageEventEnum {

    LOGIN(1), WITHDRAW(2), TRANSFER(3);

    private int code;

    public int getCode() {
        return code;
    }

    NotificationMessageEventEnum(int code) {
        this.code = code;
    }
}
