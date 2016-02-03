package me.exrates.security.service;

import java.util.List;

import me.exrates.model.User;

public interface UserSecureService {

public List<User> getAllUsers();

public List<String> getUserRoles(String email);
}
