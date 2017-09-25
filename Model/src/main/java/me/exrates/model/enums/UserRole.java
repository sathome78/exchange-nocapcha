package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedUserRoleIdException;

import java.util.Arrays;

public enum UserRole {

  ADMINISTRATOR(1),
  ACCOUNTANT(2),
  ADMIN_USER(3),
  USER(4),
  ROLE_CHANGE_PASSWORD(5),
  EXCHANGE(6),
  VIP_USER(7),
  TRADER(8),
  FIN_OPERATOR(9),
  BOT_TRADER(10);

  private final int role;

  UserRole(int role) {
    this.role = role;
  }

  public int getRole() {
    return role;
  }

  public static UserRole convert(int id) {
    return Arrays.stream(UserRole.class.getEnumConstants())
        .filter(e -> e.role == id)
        .findAny()
        .orElseThrow(() -> new UnsupportedUserRoleIdException(String.valueOf(id)));
  }

  @Override
  public String toString() {
    return "UserRole{" +
            "role=" + role + " " + this.name() +
            '}';
  }
}