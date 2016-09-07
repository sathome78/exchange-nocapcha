package me.exrates.service;

import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.dto.UserSessionInfoDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.exception.UnRegisteredUserDeleteException;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public interface UserService {

    int getIdByEmail(String email);

    User findByEmail(String email);

    void createUserFile(int userId, List<Path> paths);

    void deleteUserFile(int docId);

    List<UserFile> findUserDoc(int userId);

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

    void sendUnfamiliarIpNotificationEmail(User user, String emailSubject, String emailText, Locale locale);

    boolean createTemporalToken(TemporalToken token);

    User getCommonReferralRoot();

    void updateCommonReferralRoot(int userId);

    /**
     * Returns preferred locale for user stored in DB
     * @param userId
     * @return string mnemonic of locale
     */
    String getPreferedLang(int userId);

    /**
     * Stores preferred locale for user in DB
     * @param userId
     * @param locale
     * @return "true" if data saved successfully, or "false" if none
     */
    boolean setPreferedLang(int userId, Locale locale);

    /**
     * Stores IP-address in DB for user. Data is stored in table USER_IP
     * @param email is email the user as his identifier
     * @param ip is IP-address
     * @return "true" if data saved successfully, or "false" if none
     */
    boolean insertIp(String email, String ip);

    /**
     * Returns IP-address state for user
     * @param email is email for search the user
     * @param ip is IP-address for check
     * @return one of the values the enum UserIpState: CONFIRMED or NOT_CONFIRMED
     */
    public UserIpDto getUserIpState(String email, String ip);

    /**
     * Saves in DB last date for IP? when user auth successfully
     * @param userId is ID the user
     * @param ip is ip-address from which user auth
     * @return "true" if data saved successfully, or "false" if none
     */
    boolean setLastRegistrationDate(int userId, String ip);

    /**
     * Returns user's info, including wallet balance and turnover for period
     * Used to unload data to csv file
     *
     * @param startDate is the begin the period (including)
     * @param endDate is the end the period (including)
     * @return list the UserSummaryDto
     * @author ValkSam
     */
    List<UserSummaryDto> getUsersSummaryList(String startDate, String endDate);

    List<UserSessionInfoDto> getUserSessionInfo(Set<String> emails);
}
