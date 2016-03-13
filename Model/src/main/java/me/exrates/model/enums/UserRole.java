package me.exrates.model.enums;

public enum UserRole {

    ADMINISTRATOR(1),
    ACCOUNTANT(2),
    ADMIN_USER(3),
    USER(4);

    private final int role;

    UserRole(int role) {
        this.role = role;
    }

    public int getRole() {
        return role;
    }
}