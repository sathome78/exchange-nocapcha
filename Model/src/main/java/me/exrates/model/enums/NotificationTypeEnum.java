package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedNewsTypeIdException;

import java.util.Arrays;

/**
 * Created by Maks on 29.09.2017.
 */
public enum NotificationTypeEnum {

    EMAIL(1, false, null), SMS(2, true, "message_price"), TELEGRAM(3, true, "subscribe_price");

    private int code;
    private String priceColumn;

    private boolean needSubscribe;

    public String getPriceColumn() {
        return priceColumn;
    }

    NotificationTypeEnum(int code, boolean needSubscribe, String priceColumn) {
        this.code = code;
        this.needSubscribe = needSubscribe;
        this.priceColumn = priceColumn;
    }

    public boolean isNeedSubscribe() {
        return needSubscribe;
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