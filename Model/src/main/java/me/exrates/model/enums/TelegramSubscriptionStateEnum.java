package me.exrates.model.enums;

/**
 * Created by Maks on 05.10.2017.
 */
public enum TelegramSubscriptionStateEnum {

    SUBSCRIBED(false, true, null),
    WAIT_FOR_SUBSCRIBE(true, false, SUBSCRIBED);

    public static TelegramSubscriptionStateEnum getBeginState() {
        return WAIT_FOR_SUBSCRIBE;
    }

    TelegramSubscriptionStateEnum(boolean beginState,  boolean finalState, TelegramSubscriptionStateEnum nextState) {
        this.beginState = beginState;
        this.nextState = nextState;
        this.finalState = finalState;
    }

    boolean beginState;

    boolean finalState;

    TelegramSubscriptionStateEnum nextState;


    public TelegramSubscriptionStateEnum getNextState() {
        return SUBSCRIBED;
    }

    public boolean isBeginState() {
        return beginState;
    }

    public boolean isFinalState() {
        return finalState;
    }
}
