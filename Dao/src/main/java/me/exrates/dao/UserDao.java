package me.exrates.dao;

import java.util.List;

import me.exrates.model.User;

public interface UserDao {

	boolean create(User user);

	boolean addUserRoles(String email, String role);

	User findByEmail(String email);

	String getBriefInfo(int login);

	boolean ifNicknameIsUnique(String nickname);

	boolean ifPhoneIsUnique(int phone);

	boolean ifEmailIsUnique(String email);

	String getIP(int userId);

	boolean setIP(int id, String ip);

	int getIdByEmail(String email);

	boolean addIPToLog(int userId, String ip);

	void update(User user);

	void delete(User user);

	List<User> getAllUsers();

	List<String> getUserRoles(String email);
}