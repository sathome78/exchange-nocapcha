package me.exrates.daos;

import java.util.List;

import me.exrates.beans.User;



public interface UserDao {
	
	public boolean create(User user);
	
	public boolean addUserRoles(String email, String role);

	public String getBriefInfo(int login);
	
	public boolean ifNicknameIsUnique(String nickname);
	
	public boolean ifPhoneIsUnique(int phone);
	
	public boolean ifEmailIsUnique(String email);
	
	public String getIP(int userId);
	
	public boolean setIP(int id, String ip);
	
	public int getIdByEmail(String email);
	
	public boolean addIPToLog(int userId, String ip);
	
	public void update(User user); 
	
	public void delete(User user); 
	
	public List<User> getAllUsers();
	
	public List<String> getUserRoles(String email);
}
