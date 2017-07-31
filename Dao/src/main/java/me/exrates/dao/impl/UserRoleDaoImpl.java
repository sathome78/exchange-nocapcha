package me.exrates.dao.impl;

import me.exrates.dao.UserRoleDao;
import me.exrates.model.UserRoleSettings;
import me.exrates.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonMap;

/**
 * Created by ValkSam on 09.03.2017.
 */
@Repository
public class UserRoleDaoImpl implements UserRoleDao {
  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public List<UserRole> findRealUserRoleIdByBusinessRoleList(String businessRoleName) {
    String sql = "SELECT UR.id " +
        "  FROM USER_ROLE UR " +
        "  JOIN USER_ROLE_BUSINESS_FEATURE URBF ON (URBF.id = UR.user_role_business_feature_id) AND (URBF.name = :business_role_name) ";
    return namedParameterJdbcTemplate.query(sql, singletonMap("business_role_name", businessRoleName), (resultSet, i) -> {
      return UserRole.convert(resultSet.getInt("id"));
    });
  }

  @Override
  public List<UserRole> findRealUserRoleIdByGroupRoleList(String groupRoleName) {
    String sql = "SELECT UR.id " +
        "  FROM USER_ROLE UR " +
        "  JOIN USER_ROLE_GROUP_FEATURE URGF ON (URGF.id = UR.user_role_group_feature_id) AND (URGF.name = :group_role_name) ";
    return namedParameterJdbcTemplate.query(sql, singletonMap("group_role_name", groupRoleName), (resultSet, i) -> {
      return UserRole.convert(resultSet.getInt("id"));
    });
  }

  @Override
  public boolean isOrderAcceptionAllowedForUser(Integer userId) {
    String sql = "SELECT order_acception_same_role_only FROM USER_ROLE_SETTINGS where user_role_id = (SELECT roleid FROM USER WHERE id = :user_id)";
    return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("user_id", userId), Boolean.class);
  }

  @Override
  public boolean isOrderFilteringEnabled(Integer roleId) {
    String sql = "SELECT order_filtering_enabled FROM USER_ROLE_SETTINGS where user_role_id = :user_role_id";
    return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("user_role_id", roleId), Boolean.class);
  }

  @Override
  public UserRoleSettings retrieveSettingsForRole(Integer roleId) {
    String sql = "SELECT user_role_id, order_acception_same_role_only, order_filtering_enabled FROM USER_ROLE_SETTINGS where user_role_id = :user_role_id";
    return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("user_role_id", roleId), (rs, rowNum) -> {
      UserRoleSettings settings = new UserRoleSettings();
      settings.setUserRole(UserRole.convert(rs.getInt("user_role_id")));
      settings.setOrderAcceptionSameRoleOnly(rs.getBoolean("order_acception_allowed"));
      settings.setOrderFilteringEnabled(rs.getBoolean("order_filtering_enabled"));
      return settings;
    });
  }

}
