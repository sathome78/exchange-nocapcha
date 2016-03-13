package me.exrates.service;

import me.exrates.model.User;
import me.exrates.model.enums.UserRole;

import java.util.List;

public interface UserService {

    int getIdByEmail(String email);

    User findByEmail(String email);

    boolean create(User user);

    boolean ifNicknameIsUnique(String nickname);

    boolean ifEmailIsUnique(String email);

    String logIP(String email, String host);

    public void verifyUserEmail(String token);

    List<UserRole> getAllRoles();

    User getUserById(int id);

    boolean createUserByAdmin(User user);

    boolean updateUserByAdmin(User user);
}
