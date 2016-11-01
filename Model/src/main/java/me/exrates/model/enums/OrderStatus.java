package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedOrderStatusException;

public enum OrderStatus {

    INPROCESS(1),
    OPENED(2),
    CLOSED(3),
    CANCELLED(4),
    DELETED(5),
    DRAFT(6),
    SPLIT(7);

    private final int status;

    OrderStatus(int status) {
        this.status = status;
    }

    public static OrderStatus convert(int id) {
        switch (id) {
            case 1:
                return INPROCESS;
            case 2:
                return OPENED;
            case 3:
                return CLOSED;
            case 4:
                return CANCELLED;
            case 5:
                return DELETED;
            case 6:
                return DRAFT;
            case 7:
                return SPLIT;
            default:
                throw new UnsupportedOrderStatusException(id);
        }
    }

    public int getStatus() {
        return status;
    }
}
