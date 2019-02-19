package me.exrates.security.service;

import me.exrates.model.User;
import me.exrates.model.dto.UserShortDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.enums.UserRole;

import java.util.List;
import java.util.Map;

public interface UserSecureService {

    UserShortDto getUserByUsername(String email);

    List<User> getAllUsers();

    List<User> getUsersByRoles(List<UserRole> listRoles);

    DataTable<List<User>> getUsersByRolesPaginated(List<UserRole> listRoles, Map<String, String> tableParams);

    UserRole getUserRoles(String email);

    List<String> getUserAuthorities(String email);
}
