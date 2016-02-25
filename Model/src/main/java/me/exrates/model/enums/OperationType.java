package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedOperationTypeException;

public enum OperationType {

	INPUT(1),
	OUTPUT(2),
	SELL(3),
	BUY(4);
	
    public final int type;

    OperationType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static OperationType convert(int tupleId) {
        switch (tupleId) {
            case 1 : return INPUT;
            case 2 : return OUTPUT;
            case 3 : return SELL;
            case 4 : return BUY;
            default:
                throw new UnsupportedOperationTypeException(tupleId);
        }
    }
}
