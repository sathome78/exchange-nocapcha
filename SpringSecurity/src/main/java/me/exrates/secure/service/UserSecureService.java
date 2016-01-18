package me.exrates.secure.service;

import java.util.List;

import me.exrates.beans.User;

public interface UserSecureService {

public List<User> getAllUsers();

public List<String> getUserRoles(String email);
}
