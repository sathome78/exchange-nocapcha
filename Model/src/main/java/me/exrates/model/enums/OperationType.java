package me.exrates.model.enums;

public enum OperationType {

	SELL(3),
	BUY(4);
	
    public final int type;

    OperationType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
