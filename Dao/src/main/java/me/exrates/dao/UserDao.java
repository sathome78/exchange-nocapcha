package me.exrates.dao;

import java.util.List;

import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;

public interface UserDao {

	boolean create(User user);

	List<UserRole> getAllRoles();

	List<User> getUsersByRoles(List<UserRole> listRoles);

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

	boolean update(User user);

	List<User> getAllUsers();

	User getUserById(int id);

	UserRole getUserRoles(String email);

	boolean createTemporalToken(TemporalToken token);

	TemporalToken verifyToken(String token);

	boolean deleteTemporalToken(TemporalToken token);

	boolean deleteTemporalTokensOfTokentypeForUser(TemporalToken token);

	List<TemporalToken> getTokenByUserAndType(int userId, TokenType tokenType);

	boolean updateUserStatus(User user);

	List<TemporalToken> getAllTokens();

	boolean delete(User user);
}