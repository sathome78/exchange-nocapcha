package me.exrates.service.impl;


import me.exrates.dao.UserDao;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.mobileApiDto.TemporaryPasswordDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.service.NotificationService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.exception.*;
import me.exrates.service.exception.api.UniqueEmailConstraintException;
import me.exrates.service.exception.api.UniqueNicknameConstraintException;
import me.exrates.service.token.TokenScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TokenScheduler tokenScheduler;

    private final int USER_FILES_THRESHOLD = 3;

    private final Set<String> USER_ROLES = Stream.of(UserRole.values()).map(UserRole::name).collect(Collectors.toSet());
    private final UserRole ROLE_DEFAULT_COMMISSION = UserRole.USER;

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    @Transactional(rollbackFor = Exception.class)
    public boolean create(User user, Locale locale) {
        Boolean flag = false;
        if (this.ifEmailIsUnique(user.getEmail())) {
            if (this.ifNicknameIsUnique(user.getNickname())) {
                if (userDao.create(user) && userDao.insertIp(user.getEmail(), user.getIp())) {
                    int user_id = this.getIdByEmail(user.getEmail());
                    user.setId(user_id);
                    sendEmailWithToken(user, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale);
                    flag = true;
                }
            }
        }
        return flag;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUserRest(User user, Locale locale) {
        if (!ifEmailIsUnique(user.getEmail())) {
            LOGGER.error("Email already exists!");
            throw new UniqueEmailConstraintException("Email already exists!");
        }
        if (!ifNicknameIsUnique(user.getNickname())) {
            LOGGER.error("Nickname already exists!");
            throw new UniqueNicknameConstraintException("Nickname already exists!");
        }
        Boolean result = userDao.create(user) && userDao.insertIp(user.getEmail(), user.getIp());
        if (result) {
            int user_id = this.getIdByEmail(user.getEmail());
            user.setId(user_id);
            userDao.setPreferredLang(user_id, locale);
            sendEmailWithToken(user, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale);
        }
        return result;
    }

    /**
     * Verifies user by token that obtained by the redirection from email letter
     * if the verifying is success, all token corresponding type of this user will be deleted
     * if there are jobs for deleted tokens in scheduler, they will be deleted from queue.
     */
    @Transactional(rollbackFor = Exception.class)
    public int verifyUserEmail(String token) {
        TemporalToken temporalToken = userDao.verifyToken(token);
        //deleting all tokens related with current through userId and tokenType
        return deleteTokensAndUpdateUser(temporalToken);
    }

    private int deleteTokensAndUpdateUser(TemporalToken temporalToken) {
        if (userDao.deleteTemporalTokensOfTokentypeForUser(temporalToken)) {
            //deleting of appropriate jobs
            tokenScheduler.deleteJobsRelatedWithToken(temporalToken);
            /**/
            User user = new User();
            user.setId(temporalToken.getUserId());
            if (temporalToken.getTokenType() == TokenType.REGISTRATION ||
                    temporalToken.getTokenType() == TokenType.CHANGE_PASSWORD) {
                user.setStatus(UserStatus.ACTIVE);
                if (!userDao.updateUserStatus(user)) return 0;
            }
            if (temporalToken.getTokenType() == TokenType.REGISTRATION ||
                    temporalToken.getTokenType() == TokenType.CONFIRM_NEW_IP) {
                if (!userDao.setIpStateConfirmed(temporalToken.getUserId(), temporalToken.getCheckIp())) {
                    return 0;
                }
            }
        }
        return temporalToken.getUserId();
    }

    /*
    * for checking if there are open tokens of concrete type for the user
    * */
    public List<TemporalToken> getTokenByUserAndType(User user, TokenType tokenType) {
        return userDao.getTokenByUserAndType(user.getId(), tokenType);
    }

    public List<TemporalToken> getAllTokens() {
        return userDao.getAllTokens();
    }

    /*
    * deletes only concrete token
    * */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExpiredToken(String token) throws UnRegisteredUserDeleteException {
        boolean result = false;
        TemporalToken temporalToken = userDao.verifyToken(token);
        result = userDao.deleteTemporalToken(temporalToken);
        if (temporalToken.getTokenType() == TokenType.REGISTRATION) {
            User user = userDao.getUserById(temporalToken.getUserId());
            if (user.getStatus() == UserStatus.REGISTERED) {
                result = userDao.delete(user);
                if (!result) {
                    throw new UnRegisteredUserDeleteException();
                }
            }
        }
        return result;
    }

    public int getIdByEmail(String email) {
        return userDao.getIdByEmail(email);
    }

    @Override
    public int getIdByNickname(String nickname) {
        return userDao.getIdByNickname(nickname);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public void createUserFile(final int userId, final List<Path> paths) {
        if (findUserDoc(userId).size() == USER_FILES_THRESHOLD) {
            throw new IllegalStateException("User (id:" + userId + ") can not have more than 3 docs on the server ");
        }
        userDao.createUserDoc(userId, paths);
    }

    @Override
    public void setUserAvatar(final int userId, Path path) {
        userDao.setUserAvatar(userId, path.toString());
    }

    @Override
    public void deleteUserFile(final int docId) {
        userDao.deleteUserDoc(docId);
    }

    @Override
    public List<UserFile> findUserDoc(final int userId) {
        return userDao.findUserDoc(userId);
    }

    public boolean ifNicknameIsUnique(String nickname) {
        return userDao.ifNicknameIsUnique(nickname);
    }

    public boolean ifEmailIsUnique(String email) {
        return userDao.ifEmailIsUnique(email);
    }

    public String logIP(String email, String host) {
        int id = userDao.getIdByEmail(email);
        String userIP = userDao.getIP(id);
        if (userIP == null) {
            userDao.setIP(id, host);
        }
        userDao.addIPToLog(id, host);
        return userIP;
    }

    private String generateRegistrationToken() {
        return UUID.randomUUID().toString();

    }

    public List<UserRole> getAllRoles() {
        return userDao.getAllRoles();
    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createUserByAdmin(User user) {
        boolean result = userDao.create(user);
        if (result && user.getRole() != UserRole.USER && user.getRole() != UserRole.ROLE_CHANGE_PASSWORD) {
            return userDao.createAdminAuthoritiesForUser(userDao.getIdByEmail(user.getEmail()), user.getRole());
        }
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserByAdmin(UpdateUserDto user) {
        boolean result = userDao.update(user);
        if (result) {
            boolean hasAdminAuthorities = userDao.hasAdminAuthorities(user.getId());
            if (user.getRole() == UserRole.USER && hasAdminAuthorities) {
                return userDao.removeUserAuthorities(user.getId());
            }
            if (!hasAdminAuthorities && user.getRole() != null &&
                    user.getRole() != UserRole.USER && user.getRole() != UserRole.ROLE_CHANGE_PASSWORD) {
                return userDao.createAdminAuthoritiesForUser(user.getId(), user.getRole());
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserSettings(UpdateUserDto user) {
        return userDao.update(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(UpdateUserDto user, boolean resetPassword, Locale locale) {
        boolean changePassword = user.getPassword() != null && !user.getPassword().isEmpty();
        boolean changeFinPassword = user.getFinpassword() != null && !user.getFinpassword().isEmpty();
        if (changePassword) {
            user.setStatus(UserStatus.REGISTERED);
        }
        if (userDao.update(user)) {
            User u = new User();
            u.setId(user.getId());
            u.setEmail(user.getEmail());
            if (changePassword) {
                sendEmailWithToken(u, TokenType.CHANGE_PASSWORD, "/changePasswordConfirm", "emailsubmitChangePassword.subject", "emailsubmitChangePassword.text", locale);
            } else if (changeFinPassword) {
                sendEmailWithToken(u, TokenType.CHANGE_FIN_PASSWORD, "/changeFinPasswordConfirm", "emailsubmitChangeFinPassword.subject", "emailsubmitChangeFinPassword.text", locale);
            } else if (resetPassword) {
                sendEmailWithToken(u, TokenType.CHANGE_PASSWORD, "/resetPasswordConfirm", "emailsubmitResetPassword.subject", "emailsubmitResetPassword.text", locale);
            }
        }
        return true;
    }

    @Override
    public boolean update(UpdateUserDto user, Locale locale) {
        return update(user, false, locale);
    }


    @Override
    public void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale) {
        sendEmailWithToken(user, tokenType, tokenLink, emailSubject, emailText, locale, null);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale, String tempPass) {
        TemporalToken token = new TemporalToken();
        token.setUserId(user.getId());
        token.setValue(generateRegistrationToken());
        token.setTokenType(tokenType);
        token.setCheckIp(user.getIp());

        createTemporalToken(token);
        String tempPassId = "";
        if (tempPass != null) {
            tempPassId = "&tempId=" + userDao.saveTemporaryPassword(user.getId(), tempPass, userDao.verifyToken(token.getValue()).getId());
        }

        Email email = new Email();
        String confirmationUrl = tokenLink + "?token=" + token.getValue() + tempPassId;
        String rootUrl = "";
        if (!confirmationUrl.contains("//")) {
            rootUrl = request.getScheme() + "://" + request.getServerName() +
                    ":" + request.getServerPort();
        }
        email.setMessage(
                messageSource.getMessage(emailText, null, locale) +
                        " <a href='" +
                        rootUrl +
                        confirmationUrl +
                        "'>" + messageSource.getMessage("admin.ref", null, locale) + "</a>"
        );
        email.setSubject(messageSource.getMessage(emailSubject, null, locale));

        email.setTo(user.getEmail());
        sendMailService.sendMail(email);
    }

    @Override
    public void sendUnfamiliarIpNotificationEmail(User user, String emailSubject, String emailText, Locale locale) {
        Email email = new Email();
        email.setTo(user.getEmail());
        email.setMessage(messageSource.getMessage(emailText, new Object[]{user.getIp()}, locale));
        email.setSubject(messageSource.getMessage(emailSubject, null, locale));
        sendMailService.sendInfoMail(email);
    }

    public boolean createTemporalToken(TemporalToken token) {
        boolean result = userDao.createTemporalToken(token);
        if (result) {
            tokenScheduler.initTrigers();
        }
        return result;
    }

    @Override
    public User getCommonReferralRoot() {
        try {
            return userDao.getCommonReferralRoot();
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void checkFinPassword(String enteredFinPassword, User storedUser, Locale locale) {
        boolean isNotConfirmedToken = getTokenByUserAndType(storedUser, TokenType.CHANGE_FIN_PASSWORD).size() > 0;
        if (isNotConfirmedToken) {
            throw new NotConfirmedFinPasswordException(messageSource.getMessage("admin.notconfirmedfinpassword", null, locale));
        }
        String currentFinPassword = storedUser.getFinpassword();
        if (currentFinPassword == null || currentFinPassword.isEmpty()) {
            throw new AbsentFinPasswordException(messageSource.getMessage("admin.absentfinpassword", null, locale));
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean authSuccess = passwordEncoder.matches(enteredFinPassword, currentFinPassword);
        if (!authSuccess) {
            throw new WrongFinPasswordException(messageSource.getMessage("admin.wrongfinpassword", null, locale));
        }
    }

    @Override
    public void updateCommonReferralRoot(final int userId) {
        userDao.updateCommonReferralRoot(userId);
    }

    @Override
    public String getPreferedLang(int userId) {
        return userDao.getPreferredLang(userId);
    }

    @Override
    public String getPreferedLangByEmail(String email) {
        return userDao.getPreferredLangByEmail(email);
    }

    @Override
    public boolean setPreferedLang(int userId, Locale locale) {
        return userDao.setPreferredLang(userId, locale);
    }

    @Override
    public boolean insertIp(String email, String ip) {
        return userDao.insertIp(email, ip);
    }

    @Override
    public UserIpDto getUserIpState(String email, String ip) {
        return userDao.getUserIpState(email, ip);
    }

    @Override
    public boolean setLastRegistrationDate(int userId, String ip) {
        return userDao.setLastRegistrationDate(userId, ip);
    }

    @Override
    public List<UserSummaryDto> getUsersSummaryList(String startDate, String endDate, List<Integer> roles) {
        return userDao.getUsersSummaryList(startDate, endDate, roles);
    }

    @Override
    public void saveTemporaryPasswordAndNotify(UpdateUserDto user, String temporaryPass, Locale locale) {
        user.setStatus(UserStatus.REGISTERED);
        if (userDao.update(user)) {
            User u = new User();
            u.setId(user.getId());
            u.setEmail(user.getEmail());
            sendEmailWithToken(u, TokenType.CHANGE_PASSWORD, "/rest/user/resetPasswordConfirm", "emailsubmitResetPassword.subject", "emailsubmitResetPassword.text", locale, temporaryPass);
        }
    }

    @Override
    public boolean replaceUserPassAndDelete(String token, Long tempPassId) {
        TemporalToken temporalToken = userDao.verifyToken(token);

        if (temporalToken != null) {
            TemporaryPasswordDto dto = userDao.getTemporaryPasswordById(tempPassId);
            LOGGER.debug(dto);
            if (LocalDateTime.now().isAfter(dto.getDateCreation().plusDays(1L))) {
                removeTemporaryPassword(dto.getId());
                throw new ResetPasswordExpirationException("Password expired");
            }
            userDao.updateUserPasswordFromTemporary(tempPassId);
            removeTemporaryPassword(tempPassId);
            return deleteTokensAndUpdateUser(temporalToken) > 0;
        }
        removeTemporaryPassword(tempPassId);
        throw new TokenNotFoundException("Cannot find token");


    }

    @Override
    public boolean removeTemporaryPassword(Long id) {
        return userDao.deleteTemporaryPassword(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean tempDeleteUser(String email) {
        int id = userDao.getIdByEmail(email);
        LOGGER.debug(id);
        boolean result = userDao.tempDeleteUserWallets(id) && userDao.tempDeleteUser(id);
        if (!result)  {
            throw new RuntimeException("Could not delete");
        }
        return result;
    }


    @Override
    public List<UserSummaryInOutDto> getUsersSummaryInOutList(String startDate, String endDate, List<Integer> roles) {
        return userDao.getUsersSummaryInOutList(startDate, endDate, roles);
    }

    @Override
    public List<UserSummaryTotalInOutDto> getUsersSummaryTotalInOutList(String startDate, String endDate, List<Integer> roles) {
        return userDao.getUsersSummaryTotalInOutList(startDate, endDate, roles);
    }

    @Override
    public List<UserSummaryOrdersDto> getUserSummaryOrdersList(String startDate, String endDate, List<Integer> roles) {
        return userDao.getUserSummaryOrdersList(startDate, endDate, roles);
    }

    @PostConstruct
    private void initTokenTriggers() {
        tokenScheduler.initTrigers();
    }

    @Override
    public List<UserSessionInfoDto> getUserSessionInfo(Set<String> emails) {
        return userDao.getUserSessionInfo(emails);
    }

    @Override
    public String getAvatarPath(Integer userId) {
        return userDao.getAvatarPath(userId);
    }

    @Override
    public Locale getUserLocaleForMobile(String email) {
        String lang = getPreferedLangByEmail(email);
        //adaptation for locales available in mobile app
        if (!("ru".equalsIgnoreCase(lang) || "en".equalsIgnoreCase(lang))) {
            lang = "en";
        }

        return new Locale(lang);
    }

    @Override
    public Collection<Comment> getUserComments(int id){

        return userDao.getUserComments(id);

    }

    @Override
    public boolean addUserComment(String newComment, String email, boolean sendMessage, Locale locale){

        User user = findByEmail(email);
        User creator;
        Comment comment = new Comment();
        comment.setMessageSent(sendMessage);
        comment.setUser(user);
        comment.setComment(newComment);
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            creator = findByEmail(auth.getName());
            comment.setCreator(creator);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        boolean success = userDao.addUserComment(comment);

        if (comment.isMessageSent()){
            notificationService.notifyUser(user.getId(), NotificationEvent.ADMIN, "admin.subjectCommentTitle",
                    "admin.subjectCommentMessage", new Object[]{": " + newComment});
        }

        return success;
    }

    @Override
    public boolean deleteUserComment(int id) {
        return userDao.deleteUserComment(id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AdminAuthorityOption> getAuthorityOptionsForUser(Integer userId, Set<String> allowedAuthorities, Locale locale) {
        return userDao.getAuthorityOptionsForUser(userId).stream()
                .filter(option -> allowedAuthorities.contains(option.getAdminAuthority().name()))
                .peek(option -> option.localize(messageSource, locale))
                .collect(Collectors.toList());
    }

    @Override
    public void updateAdminAuthorities(List<AdminAuthorityOption> options, Integer userId, String currentUserEmail) {
        UserRole currentUserRole = userDao.getUserRoles(currentUserEmail);
        UserRole updatedUserRole = userDao.getUserRoleById(userId);
        if (currentUserRole != UserRole.ADMINISTRATOR && updatedUserRole == UserRole.ADMINISTRATOR) {
            throw new ForbiddenOperationException("Status modification not permitted");
        }
        userDao.updateAdminAuthorities(options, userId);

    }

    @Override
    public UserRole getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.debug("Authentication: " + authentication);
        String grantedAuthority = authentication.getAuthorities().
                stream().map(GrantedAuthority::getAuthority)
                .filter(USER_ROLES::contains)
                .findFirst().orElse(ROLE_DEFAULT_COMMISSION.name());
        LOGGER.debug("Granted authority: " + grantedAuthority);
        return UserRole.valueOf(grantedAuthority);

    }

    @Override
    public List<Integer> resolveRoleIdsByName(String roleName) {
        List<UserRole> userRoles;
        if ("ADMIN".equals(roleName)) {
            userRoles = Arrays.asList(UserRole.ADMINISTRATOR, UserRole.ACCOUNTANT, UserRole.ADMIN_USER);
        }else if("ALL".equals(roleName)){
            return new ArrayList<>();
        }else {
            userRoles = Collections.singletonList(UserRole.valueOf(roleName));
        }
        return userRoles.stream().map(UserRole::getRole).collect(Collectors.toList());
    }

}