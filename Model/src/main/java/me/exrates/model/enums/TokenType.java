package me.exrates.model.enums;

public enum TokenType {
    REGISTRATION(1),
    CHANGE_PASSWORD(2),
    CHANGE_FIN_PASSWORD(3);

    private final int tokenType;

    TokenType(int tokenType) {
        this.tokenType = tokenType;
    }

    public int getTokenType() {
        return tokenType;
    }
}
