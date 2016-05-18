package me.exrates.dao;

import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;

import java.nio.file.Path;
import java.util.List;

import java.util.List;
import java.util.Locale;

public interface UserDao {

    boolean create(User user);

	void createUserDoc(int userId, List<Path> paths);

	List<UserFile> findUserDoc(int userId);

	void deleteUserDoc(int docId);

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

    boolean update(UpdateUserDto user);

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

    String getPreferredLang(int userId);

    boolean setPreferredLang(int userId, Locale locale);

    boolean insertIp(String email, String ip);

    public UserIpDto getUserIpState(String email, String ip);

    boolean setIpStateConfirmed(int userId, String ip);

    boolean setLastRegistrationDate(int userId, String ip);

    List<UserSummaryDto> getUsersSummaryList(String startDate, String endDate);
}