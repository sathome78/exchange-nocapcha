package me.exrates.dao;

import java.util.List;

import me.exrates.model.User;



public interface UserDao {
<<<<<<< HEAD
	
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
=======
	boolean create(User user);
	boolean addUserRoles(String email, String role);
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
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
