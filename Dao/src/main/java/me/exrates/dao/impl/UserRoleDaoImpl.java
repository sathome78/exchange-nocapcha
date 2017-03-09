package me.exrates.dao.impl;

import me.exrates.dao.UserRoleDao;
import me.exrates.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
