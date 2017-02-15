package me.exrates.dao;

import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.mobileApiDto.TemporaryPasswordDto;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public interface UserDao {

    int getIdByNickname(String nickname);

    boolean create(User user);

	void createUserDoc(int userId, List<Path> paths);

    void setUserAvatar(int userId, String path);

    List<UserFile> findUserDoc(int userId);

	void deleteUserDoc(int docId);

	List<UserRole> getAllRoles();

    List<User> getUsersByRoles(List<UserRole> listRoles);

    UserRole getUserRoleById(Integer id);

    List<String> getUserRoleAndAuthorities(String email);

    List<AdminAuthorityOption> getAuthorityOptionsForUser(Integer userId);

    boolean createAdminAuthoritiesForUser(Integer userId, UserRole role);

    boolean hasAdminAuthorities(Integer userId);

    void updateAdminAuthorities(List<AdminAuthorityOption> options, Integer userId);

    boolean removeUserAuthorities(Integer userId);

    boolean addUserRoles(String email, String role);

    User findByEmail(String email);

    PagingData<List<User>> getUsersByRolesPaginated(List<UserRole> roles, int offset, int limit,
                                                    String orderColumnName, String orderDirection,
                                                    String searchValue);

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

    User getCommonReferralRoot();

    void updateCommonReferralRoot(int userId);

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

    String getPreferredLangByEmail(String email);

    boolean insertIp(String email, String ip);

    UserIpDto getUserIpState(String email, String ip);

    boolean setIpStateConfirmed(int userId, String ip);

    boolean setLastRegistrationDate(int userId, String ip);

    List<UserSummaryDto> getUsersSummaryList(String startDate, String endDate, List<Integer> roles);

    Long saveTemporaryPassword(Integer userId, String password, Integer tokenId);

    TemporaryPasswordDto getTemporaryPasswordById(Long id);

    boolean updateUserPasswordFromTemporary(Long tempPassId);

    boolean deleteTemporaryPassword(Long id);

    boolean tempDeleteUser(int id);

    boolean tempDeleteUserWallets(int userId);

    List<UserSessionInfoDto> getUserSessionInfo(Set<String> emails);

    List<UserSummaryInOutDto> getUsersSummaryInOutList(String startDate, String endDate, List<Integer> roles);

    List<UserSummaryTotalInOutDto> getUsersSummaryTotalInOutList(String startDate, String endDate, List<Integer> roles);

    List<UserSummaryOrdersDto> getUserSummaryOrdersList(String startDate, String endDate, List<Integer> roles);

    String getAvatarPath(Integer userId);

    Collection<Comment> getUserComments(int id);

    boolean addUserComment(Comment comment);

    boolean deleteUserComment(int id);

    Integer retrieveNicknameSearchLimit();

    List<String> findNicknamesByPart(String part, Integer limit);
}
