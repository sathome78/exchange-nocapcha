package me.exrates.service;

import me.exrates.model.UserRoleSettings;
import me.exrates.model.enums.BusinessUserRoleEnum;
import me.exrates.model.enums.GroupUserRoleEnum;
import me.exrates.model.enums.UserRole;

import java.util.List;

public interface UserRoleService {

  List<UserRole> getRealUserRoleByBusinessRoleList(BusinessUserRoleEnum businessUserRoleEnum);

  List<Integer> getRealUserRoleIdByBusinessRoleList(BusinessUserRoleEnum businessUserRoleEnum);

  String[] getRealUserRoleNameByBusinessRoleArray(BusinessUserRoleEnum businessUserRoleEnum);

  List<Integer> getRealUserRoleIdByBusinessRoleList(String businessUserRoleName);

  List<UserRole> getRealUserRoleByGroupRoleList(GroupUserRoleEnum groupUserRoleEnum);

  List<Integer> getRealUserRoleIdByGroupRoleList(GroupUserRoleEnum groupUserRoleEnum);

  List<Integer> getRealUserRoleIdByGroupRoleList(String groupUserRoleName);

  boolean isOrderAcceptionAllowedForUser(Integer userId);

  boolean isOrderFilteringEnabled(Integer roleId);

  UserRoleSettings retrieveSettingsForRole(Integer roleId);
}
