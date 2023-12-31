package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedOrderTypeException;

import java.util.Arrays;

/**
 * Created by maks on 21.04.2017.
 */
public enum OrderBaseType {

    LIMIT(1), STOP_LIMIT(2), ICO(3), MARKET(4);

    private int code;

    public int getType() {
        return code;
    }

    OrderBaseType(int code) {
        this.code = code;
    }

    public static OrderBaseType convert(int code) {
        return Arrays.stream(OrderBaseType.values()).filter(ot -> ot.code == code).findAny()
                .orElseThrow(UnsupportedOrderTypeException::new);
    }

    public static OrderBaseType convert(String name) {
        return Arrays.stream(OrderBaseType.values()).filter(ot -> ot.name().equalsIgnoreCase(name)).findAny()
                .orElseThrow(UnsupportedOrderTypeException::new);
    }
}
