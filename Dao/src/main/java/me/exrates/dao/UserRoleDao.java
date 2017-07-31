package me.exrates.dao;

import me.exrates.model.UserRoleSettings;
import me.exrates.model.enums.UserRole;

import java.util.List;

public interface UserRoleDao {
  List<UserRole> findRealUserRoleIdByBusinessRoleList(String businessRoleName);

  List<UserRole> findRealUserRoleIdByGroupRoleList(String businessRoleName);

    boolean isOrderAcceptionAllowedForUser(Integer userId);

    boolean isOrderFilteringEnabled(Integer roleId);

    UserRoleSettings retrieveSettingsForRole(Integer roleId);
}
