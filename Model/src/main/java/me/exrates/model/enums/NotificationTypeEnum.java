package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedNewsTypeIdException;

import java.util.Arrays;

/**
 * Created by Maks on 29.09.2017.
 */
public enum NotificationTypeEnum {

    SMS(1), EMAIL(2), TELEGRAM(3), WEIBO(4);

    private int code;

    NotificationTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static NotificationTypeEnum convert(int id) {
        return Arrays.stream(NotificationTypeEnum.class.getEnumConstants())
                .filter(e -> e.code == id)
                .findAny()
                .orElseThrow(() -> new RuntimeException("invalid id " + String.valueOf(id)));
    }
}
