package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedOperationTypeException;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum OperationType {
    INPUT(1),
    OUTPUT(2),
    SELL(3),
    BUY(4),
    WALLET_INNER_TRANSFER(5),
    REFERRAL(6),
    STORNO(7),
    MANUAL(8),
    USER_TRANSFER(9),
    AMOUNT_CORRECTION(10);

    public final int type;

    OperationType(int type) {
        this.type = type;
    }

    public static List<OperationType> getInputOutputOperationsList(){
        return new ArrayList<OperationType>(){{
            add(INPUT);
            add(OUTPUT);
        }};
    }

    public static OperationType convert(int tupleId) {
        switch (tupleId) {
            case 1:
                return INPUT;
            case 2:
                return OUTPUT;
            case 3:
                return SELL;
            case 4:
                return BUY;
            case 5:
                return WALLET_INNER_TRANSFER;
            case 6:
                return REFERRAL;
            case 7:
                return STORNO;
            case 8:
                return MANUAL;
            case 9:
                return USER_TRANSFER;
            case 10:
                return AMOUNT_CORRECTION;
            default:
                throw new UnsupportedOperationTypeException(tupleId);
        }
    }

    public static OperationType getOpposite(OperationType ot) {
        switch (ot) {
            case INPUT:
                return OUTPUT;
            case OUTPUT:
                return INPUT;
            case SELL:
                return BUY;
            case BUY:
                return SELL;
            default:
                return ot;
        }
    }

    public int getType() {
        return type;
    }

    public String toString(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("operationtype." + this.name(), null, locale);

    }
}
