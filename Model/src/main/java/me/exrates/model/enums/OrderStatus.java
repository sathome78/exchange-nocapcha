package me.exrates.model.enums;

public enum OrderStatus {

	INPROCESS(1),
	OPENED(2),
	CLOSED(3),
	CANCELLED(4),
	DELETED(5),
	DRAFT(6);

    public final int status;

    OrderStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
