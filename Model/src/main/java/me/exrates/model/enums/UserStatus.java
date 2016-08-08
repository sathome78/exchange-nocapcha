package me.exrates.model.enums;

public enum UserStatus {

	REGISTERED(1),
	ACTIVE(2),
	DELETED(3),
    BANNED_IN_CHAT(4);

    private final int status;

    UserStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
