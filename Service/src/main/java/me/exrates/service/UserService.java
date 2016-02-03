package me.exrates.service;

import me.exrates.model.User;


public interface UserService {  

public int getIdByEmail(String email);  

public boolean create(User user);

public boolean ifNicknameIsUnique(String nickname);

public boolean ifEmailIsUnique(String email);

public String logIP(String email, String host);


}  