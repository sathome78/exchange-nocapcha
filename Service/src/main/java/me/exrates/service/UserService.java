package me.exrates.service;

import me.exrates.model.User;


public interface UserService {

    int getIdByEmail(String email);

    boolean create(User user);

    boolean ifNicknameIsUnique(String nickname);

    boolean ifEmailIsUnique(String email);

    String logIP(String email, String host);
}