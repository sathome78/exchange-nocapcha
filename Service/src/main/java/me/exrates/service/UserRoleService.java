package me.exrates.service;

import me.exrates.model.enums.BusinessUserRoleEnum;
import me.exrates.model.enums.GroupUserRoleEnum;
import me.exrates.model.enums.UserRole;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRoleService {

  List<UserRole> getRealUserRoleByBusinessRoleList(BusinessUserRoleEnum businessUserRoleEnum);

  List<Integer> getRealUserRoleIdByBusinessRoleList(BusinessUserRoleEnum businessUserRoleEnum);

  List<Integer> getRealUserRoleIdByBusinessRoleList(String businessUserRoleName);

  List<UserRole> getRealUserRoleByGroupRoleList(GroupUserRoleEnum groupUserRoleEnum);

  List<Integer> getRealUserRoleIdByGroupRoleList(GroupUserRoleEnum groupUserRoleEnum);

  List<Integer> getRealUserRoleIdByGroupRoleList(String groupUserRoleName);
}
