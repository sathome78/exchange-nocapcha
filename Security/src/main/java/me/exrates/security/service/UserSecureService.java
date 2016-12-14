package me.exrates.security.service;

import java.util.List;

import me.exrates.model.User;
import me.exrates.model.enums.UserRole;

public interface UserSecureService {

public List<User> getAllUsers();

public List<User> getUsersByRoles(List<UserRole> listRoles);

public UserRole getUserRoles(String email);

    List<String> getUserAuthorities(String email);
}
