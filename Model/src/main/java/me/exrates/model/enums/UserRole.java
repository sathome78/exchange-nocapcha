package me.exrates.model.enums;

public enum UserRole {

  ADMINISTRATOR(1),
  ACCOUNTANT(2),
  ADMIN_USER(3),
  USER(4),
  ROLE_CHANGE_PASSWORD(5),
  EXCHANGE(6),
  VIP_USER(7),
  TRADER(8);

  private final int role;

  UserRole(int role) {
    this.role = role;
  }

  public int getRole() {
    return role;
  }

}