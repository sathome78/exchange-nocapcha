package me.exrates.service;

import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.exception.UnRegisteredUserDeleteException;

import java.util.List;
import java.util.Locale;

public interface UserService {

    int getIdByEmail(String email);

    User findByEmail(String email);

    boolean create(User user, Locale locale);

    boolean ifNicknameIsUnique(String nickname);

    boolean ifEmailIsUnique(String email);

    String logIP(String email, String host);

    List<TemporalToken> getTokenByUserAndType(User user, TokenType tokenType);

    int verifyUserEmail(String token);

    List<UserRole> getAllRoles();

    User getUserById(int id);

    boolean createUserByAdmin(User user);

    boolean updateUserByAdmin(UpdateUserDto user);

    boolean update(UpdateUserDto user, boolean resetPassword, Locale locale);

    boolean update(UpdateUserDto user, Locale locale);

    void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale);

    List<TemporalToken> getAllTokens();

    boolean deleteExpiredToken(String token) throws UnRegisteredUserDeleteException;

    boolean createTemporalToken(TemporalToken token);

    String getPreferedLang(int userId);

    boolean setPreferedLang(int userId, Locale locale);

    boolean insertIp(String email, String ip);

    public UserIpDto getUserIpState(String email, String ip);
}
