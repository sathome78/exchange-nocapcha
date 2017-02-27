package me.exrates.model.enums;

import lombok.NoArgsConstructor;
import me.exrates.model.exceptions.UnsupportedBusinessUserRoleNameException;
import me.exrates.model.exceptions.UnsupportedUserRoleNameException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.exrates.model.enums.UserRole.*;

/**
 * Created by ValkSam
 */

@NoArgsConstructor
public enum BusinessUserRoleEnum {
  ADMIN(ADMINISTRATOR, ACCOUNTANT, ADMIN_USER, FIN_OPERATOR),
  USER,
  EXCHANGE,
  VIP_USER,
  TRADER;

  private List<UserRole> realUserRoles = new ArrayList<>();

  BusinessUserRoleEnum(UserRole... userRole) {
    Collections.addAll(realUserRoles, userRole);
  }

  public static List<Integer> getRealUserRoleIdList(String name) {
    return getRealUserRoleList(name).stream().map(UserRole::getRole).collect(Collectors.toList());
  }

  public static List<UserRole> getRealUserRoleList(String name) {
    if ("ALL".equals(name)) {
      return Collections.emptyList();
    }
    return convert(name).getRealUserRoleList();
  }

  public List<Integer> getRealUserRoleIdList() {
    return getRealUserRoleList().stream().map(UserRole::getRole).collect(Collectors.toList());
  }

  public List<UserRole> getRealUserRoleList() {
    try {
      return this.realUserRoles.size() > 0 ? this.realUserRoles : Collections.singletonList(UserRole.valueOf(this.name()));
    } catch (IllegalArgumentException e) {
      throw new UnsupportedUserRoleNameException(this.name());
    }
  }

  public static BusinessUserRoleEnum convert(String name) {
    return Arrays.stream(BusinessUserRoleEnum.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedBusinessUserRoleNameException(name));
  }
}
