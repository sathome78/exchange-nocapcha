package me.exrates.service.impl;


import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.UserDao;
import me.exrates.dao.UserPinDao;
import me.exrates.model.AdminAuthorityOption;
import me.exrates.model.Comment;
import me.exrates.model.Email;
import me.exrates.model.PagingData;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.CallbackURL;
import me.exrates.model.dto.IpLogDto;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserBalancesDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.dto.UserSessionInfoDto;
import me.exrates.model.dto.UsersInfoDto;
import me.exrates.model.dto.api.RateDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminIpLogsFilterData;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.model.enums.PolicyEnum;
import me.exrates.model.enums.RestrictedOperation;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.enums.UserEventEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.ngExceptions.PincodeExpiredException;
import me.exrates.ngService.GeoLocationService;
import me.exrates.service.NotificationService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.UserSettingService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.exception.AbsentFinPasswordException;
import me.exrates.service.exception.AuthenticationNotAvailableException;
import me.exrates.service.exception.CallBackUrlAlreadyExistException;
import me.exrates.service.exception.CommentNonEditableException;
import me.exrates.service.exception.ForbiddenOperationException;
import me.exrates.service.exception.NotConfirmedFinPasswordException;
import me.exrates.service.exception.UnRegisteredUserDeleteException;
import me.exrates.service.exception.UserCommentNotFoundException;
import me.exrates.service.exception.WrongFinPasswordException;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.session.UserSessionService;
import me.exrates.service.token.TokenScheduler;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);
    private final static List<String> LOCALES_LIST = new ArrayList<String>() {{
        add("EN");
        add("RU");
        add("CN");
        add("ID");
        add("AR");
    }};
    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "Exrates";
    private final int USER_FILES_THRESHOLD = 3;
    private final int USER_2FA_NOTIFY_DAYS = 6;
    private final Set<String> USER_ROLES = Stream.of(UserRole.values())
            .map(UserRole::name)
            .collect(Collectors.toSet());
    private final UserRole ROLE_DEFAULT_COMMISSION = UserRole.USER;
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserSessionService userSessionService;
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
//    @Autowired
//    private ReferralService referralService;
    @Autowired
    private NotificationsSettingsService settingsService;
    @Autowired
    private G2faService g2faService;
    @Autowired
    private GeoLocationService geoLocationService;
    @Autowired
    private ExchangeApi exchangeApi;
    @Autowired
    private UserSettingService userSettingService;
    @Autowired
    private UserPinDao userPinDao;
    private Cache<String, UsersInfoDto> usersInfoCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    @Override
    public List<String> getLocalesList() {
        return LOCALES_LIST;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean create(User user, Locale locale, String source) {
        Boolean flag = false;
        if (this.ifEmailIsUnique(user.getEmail())) {
            if (this.ifNicknameIsUnique(user.getNickname())) {
                if (userDao.create(user) && userDao.insertIp(user.getEmail(), user.getIp())) {
                    int user_id = this.getIdByEmail(user.getEmail());
                    user.setId(user_id);
                    if (source != null && !source.isEmpty()) {
                        String view = "view=" + source;
                        sendEmailWithToken(user, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale, null, view);
                    } else {
                        sendEmailWithToken(user, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale);
                    }
                    flag = true;
                }
            }
        }
        return flag;
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
        return temporalToken != null ? deleteTokensAndUpdateUser(temporalToken) : 0;
    }

    private int deleteTokensAndUpdateUser(TemporalToken temporalToken) {
        if (userDao.deleteTemporalTokensOfTokentypeForUser(temporalToken)) {
            //deleting of appropriate jobs
            tokenScheduler.deleteJobsRelatedWithToken(temporalToken);
            /**/
            if (temporalToken.getTokenType() == TokenType.CONFIRM_NEW_IP) {
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
            if (user.getUserStatus() == UserStatus.REGISTERED) {
                LOGGER.debug(String.format("DELETING USER %s", user.getEmail()));
//                referralService.updateReferralParentForChildren(user);
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


    public boolean setNickname(String newNickName, String userEmail) {
        return userDao.setNickname(newNickName, userEmail);
    }

    @Override
    public boolean hasNickname(String userEmail) {
        String nickname = userDao.findByEmail(userEmail).getNickname();
        if (nickname == null || nickname.trim().length() == 0) {
            return false;
        } else return true;
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public User findByNickname(String nickname) {
        return userDao.findByNickname(nickname);
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

    @Transactional(readOnly = true)
    public boolean ifEmailIsUnique(String email) {
        return userDao.ifEmailIsUnique(email);
    }

    @Override
    public boolean userExistByEmail(String email) {
        return userDao.userExistByEmail(email);
    }

    @Override
    public void logIP(Integer id, String ip, UserEventEnum eventEnum, String url) {
        Preconditions.checkState(nonNull(id) && !StringUtils.isEmpty(ip));
        userDao.addIpToLog(id, ip, eventEnum, url);
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

        if (userDao.update(user)) {
            User u = new User();
            u.setId(user.getId());
            u.setEmail(user.getEmail());
            u.setPublicId(user.getPublicId());
            if (changePassword) {
                sendUnfamiliarIpNotificationEmail(u, "admin.changePasswordTitle", "user.settings.changePassword.successful", locale);
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
    public void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale, String tempPass, String... params) {
        TemporalToken token = new TemporalToken();
        token.setUserId(user.getId());
        token.setValue(generateRegistrationToken());
        token.setTokenType(tokenType);
        token.setCheckIp(user.getIp());
        token.setAlreadyUsed(false);

        createTemporalToken(token);
        String tempPassId = "";
        if (tempPass != null) {
            tempPassId = "&tempId=" + userDao.saveTemporaryPassword(user.getId(), tempPass, userDao.verifyToken(token.getValue()).getId());
        }

        Email email = new Email();
        StringBuilder confirmationUrl = new StringBuilder(tokenLink + "?token=" + token.getValue() + tempPassId);
        if (tokenLink.equals("/resetPasswordConfirm")) {
            confirmationUrl.append("&email=").append(user.getEmail());
        }
        String rootUrl = "";
        if (!confirmationUrl.toString().contains("//")) {
            rootUrl = request.getScheme() + "://" + request.getServerName() +
                    ":" + request.getServerPort();
        }
        if (params != null) {
            for (String patram : params) {
                confirmationUrl.append("&").append(patram);
            }
        }
        email.setMessage(
                messageSource.getMessage(emailText, null, locale) +
                        " </p><a href=\"" +
                        rootUrl +
                        confirmationUrl.toString() +
                        "\" style=\"display: block;MAX-WIDTH: 347px; FONT-FAMILY: Roboto; COLOR: #237BEF; MARGIN: auto auto .8em; font-size: 36px; line-height: 1.37; text-align: center; font-weight: 600;\">" + messageSource.getMessage("admin.ref", null, locale) + "</a>"
        );
        email.setSubject(messageSource.getMessage(emailSubject, null, locale));
        email.setTo(user.getEmail());

        Properties properties = new Properties();
        properties.setProperty("public_id", user.getPublicId());
        email.setProperties(properties);

        sendMailService.sendMail(email);

    }

    @Override
    public void sendUnfamiliarIpNotificationEmail(User user, String emailSubject, String emailText, Locale locale) {
        Email email = new Email();
        email.setTo(user.getEmail());
        email.setMessage(messageSource.getMessage(emailText, new Object[]{user.getIp()}, locale));
        email.setSubject(messageSource.getMessage(emailSubject, null, locale));

        Properties properties = new Properties();
        properties.setProperty("public_id", user.getPublicId());
        email.setProperties(properties);

        sendMailService.sendMail(email);
    }

    public boolean createTemporalToken(TemporalToken token) {
        log.info("Token is " + token);
        boolean result = userDao.createTemporalToken(token);
        if (result) {
            log.info("Token succesfully saved");
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
    public boolean removeTemporaryPassword(Long id) {
        return userDao.deleteTemporaryPassword(id);
    }

    @PostConstruct
    private void initTokenTriggers() {

//    tokenScheduler.initTrigers();
    }

    @Override
    public List<UserSessionInfoDto> getUserSessionInfo(Set<String> emails) {
        try {
            List<UserSessionInfoDto> list = userDao.getUserSessionInfo(emails);
            log.debug(Arrays.toString(list.toArray()));
            return list;
        } catch (Exception e) {
            log.error(e);
            return Collections.EMPTY_LIST;
        }
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
    public Collection<Comment> getUserComments(int id, String authenticatedAdminEmail) {
        User admin = findByEmail(authenticatedAdminEmail);
        Collection<Comment> comments = userDao.getUserComments(id);
        comments.forEach(comment -> comment.setEditable(isCommentEditable(admin.getId()).test(comment)));
        return comments;

    }

    private Predicate<Comment> isCommentEditable(int authenticatedAdminId) {
        return comment -> LocalDateTime.now().isBefore(comment.getCreationTime().plusHours(24L)) &&
                comment.getCreator().getId() == authenticatedAdminId && !comment.isMessageSent();
    }

    @Override
    public boolean addUserComment(UserCommentTopicEnum topic, String newComment, String email, boolean sendMessage) {
        Function<String, User> userFunction = this::findByEmail;

        Comment comment = new Comment();
        comment.setMessageSent(sendMessage);
        comment.setUser(userFunction.apply(email));
        comment.setComment(newComment);
        comment.setUserCommentTopic(topic);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            comment.setCreator(userFunction.apply(auth.getName()));
        } catch (Exception e) {
            LOGGER.error(e);
        }
        boolean success = userDao.addUserComment(comment);

        if (comment.isMessageSent()) {
            notificationService.notifyUser(getIdByEmail(email), NotificationEvent.ADMIN, "admin.subjectCommentTitle",
                    "admin.subjectCommentMessage", new Object[]{": " + newComment});
        }
        return success;
    }

    @Override
    public void editUserComment(int commentId, String newComment, String email, boolean sendMessage, String authenticatedAdminEmail) {
        Comment comment = userDao.getCommentById(commentId).orElseThrow(() -> new UserCommentNotFoundException("id " + commentId));
        User admin = findByEmail(authenticatedAdminEmail);
        if (isCommentEditable(admin.getId()).test(comment)) {
            userDao.editUserComment(commentId, newComment, sendMessage);
            if (sendMessage) {
                notificationService.notifyUser(comment.getUser().getId(), NotificationEvent.ADMIN, "admin.subjectCommentTitle",
                        "admin.subjectCommentMessage", new Object[]{": " + newComment});
            }
        } else {
            throw new CommentNonEditableException();
        }
    }

    @Override
    public boolean deleteUserComment(int id) {
        return userDao.deleteUserComment(id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AdminAuthorityOption> getAuthorityOptionsForUser(Integer userId, Set<String> allowedAuthorities, Locale locale) {
        return userDao.getAuthorityOptionsForUser(userId)
                .stream()
                .filter(option -> allowedAuthorities.contains(option.getAdminAuthority().name()))
                .peek(option -> option.localize(messageSource, locale))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminAuthorityOption> getActiveAuthorityOptionsForUser(Integer userId) {
        return userDao.getAuthorityOptionsForUser(userId)
                .stream()
                .filter(AdminAuthorityOption::getEnabled)
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
    public UserRole getUserRoleFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication)) {
            throw new AuthenticationNotAvailableException();
        }
        String grantedAuthority = authentication.getAuthorities().
                stream()
                .map(GrantedAuthority::getAuthority)
                .filter(USER_ROLES::contains)
                .findFirst()
                .orElse(ROLE_DEFAULT_COMMISSION.name());
        LOGGER.debug("Granted authority: " + grantedAuthority);
        return UserRole.valueOf(grantedAuthority);
    }

    @Override
    @Transactional
    public void setCurrencyPermissionsByUserId(List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList) {
        Integer userId = userCurrencyOperationPermissionDtoList.get(0).getUserId();
        userDao.setCurrencyPermissionsByUserId(
                userId,
                userCurrencyOperationPermissionDtoList
                        .stream()
                        .filter(e -> e.getInvoiceOperationPermission() != InvoiceOperationPermission.NONE)
                        .collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceOperationPermission getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(
            Integer userId,
            Integer currencyId,
            InvoiceOperationDirection invoiceOperationDirection) {
        return userDao.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(userId, currencyId, invoiceOperationDirection);
    }

    @Override
    @Transactional(readOnly = true)
    public String getEmailById(Integer id) {
        return userDao.getEmailById(id);
    }

    @Override
    public UserRole getUserRoleFromDB(String email) {
        return userDao.getUserRoleByEmail(email);
    }

    @Override
    @Transactional
    public UserRole getUserRoleFromDB(Integer userId) {
        return userDao.getUserRoleById(userId);
    }

    @Transactional
    @Override
    public String updatePinForUserForEvent(String userEmail, NotificationMessageEventEnum event) {
        String pin = String.valueOf(10000000 + new Random().nextInt(90000000));
        return userPinDao.save(pin, userEmail, event);
    }

    /*todo refator it*/
    @Override
    public boolean checkPin(String email, String pin, NotificationMessageEventEnum event) {
        int userId = getIdByEmail(email);
        NotificationsUserSetting setting = settingsService.getByUserAndEvent(userId, event);
        if (setting == null || setting.getNotificatorId() == null) {
            setting = NotificationsUserSetting.builder()
                    .notificatorId(NotificationTypeEnum.EMAIL.getCode())
                    .userId(userId)
                    .notificationMessageEventEnum(event)
                    .build();
        }
        if (setting.getNotificatorId().equals(NotificationTypeEnum.GOOGLE2FA.getCode())) {
            return g2faService.checkGoogle2faVerifyCode(pin, userId);
        }
        return pin.equals(getPinForEvent(email, event));
    }

    private String getPinForEvent(String email, NotificationMessageEventEnum event) {
        return userPinDao.findPin(email, event).orElseThrow(() -> new PincodeExpiredException(email));
    }

    @Override
    public boolean isLogin2faUsed(String email) {
        return g2faService.isGoogleAuthenticatorEnable(userDao.getIdByEmail(email));
    }


    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public String getUserEmailFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(auth)) {
            throw new AuthenticationNotAvailableException();
        }
        return auth.getName();
    }

    @Transactional(rollbackFor = Exception.class)
    public TemporalToken getTemporalTokenByValue(String token) {
        return userDao.verifyToken(token);
    }

    public User getUserByTemporalToken(String token) {
        return userDao.getUserByTemporalToken(token);
    }

    @Override
    public boolean checkPassword(int userId, String password) {
        return passwordEncoder.matches(password, userDao.getPassword(userId));
    }

    @Override
    public List<Integer> getUserFavouriteCurrencyPairs(String email) {
        User user = findByEmail(email);
        if (user == null) {
            return Collections.emptyList();
        }
        return userDao.findFavouriteCurrencyPairsById(user.getId());
    }

    @Override
    public boolean manageUserFavouriteCurrencyPair(String email, int currencyPairId, boolean delete) {
        User user = findByEmail(email);
        if (user == null) {
            return false;
        }
        return userDao.manageUserFavouriteCurrencyPair(user.getId(), currencyPairId, delete);
    }

    @Override
    public boolean deleteTempTokenByValue(String idTempToken) {
        return userDao.deleteTemporalToken(idTempToken);
    }

    @Override
    public void updateGaTag(String gaCookie, String username) {
        userDao.updateGaTag(gaCookie, username);
    }

    @Override
    public int updateReferenceIdAndStatus(String referenceId, EventStatus status) {
        return userDao.updateReferenceIdAndStatus(referenceId, status, getUserEmailFromSecurityContext());
    }

    @Override
    public String getEmailByReferenceId(String referenceId) {
        return userDao.getEmailByReferenceId(referenceId);
    }

    @Override
    public String findEmailById(int id) {
        return userDao.getEmailById(id);
    }

    @Override
    public UsersInfoDto getUsersInfoFromCache(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles) {
        String startTimeString = startTime.toString();
        String endTimeString = endTime.toString();
        final String rolesString = userRoles
                .stream()
                .map(UserRole::getName)
                .collect(joining("-"));

        String key = String.join("/", startTimeString, endTimeString, rolesString);

        try {
            return usersInfoCache.get(key, () -> this.getUsersInfoFromDatabase(startTime, endTime, userRoles));
        } catch (ExecutionException ex) {
            log.warn("Problem with return users info");
            return UsersInfoDto.builder()
                    .newUsers(0)
                    .allUsers(0)
                    .activeUsers(0)
                    .notZeroBalanceUsers(0)
                    .oneOrMoreSuccessInputUsers(0)
                    .oneOrMoreSuccessOutputUsers(0)
                    .build();
        }

    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    public UsersInfoDto getUsersInfoFromDatabase(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles) {
        UsersInfoDto usersInfo = userDao.getUsersInfo(startTime, endTime, userRoles);

        Map<Integer, List<UserBalancesDto>> usersBalances = userDao.getUserBalances(userRoles)
                .stream()
                .collect(Collectors.groupingBy(UserBalancesDto::getUserId));

        final Map<String, RateDto> ratesMap = exchangeApi.getRates();

        return usersInfo.toBuilder()
                .notZeroBalanceUsers((int) usersBalances.entrySet()
                        .stream()
                        .filter(entry -> {
                            double sum = entry.getValue()
                                    .stream()
                                    .mapToDouble(balance -> {
                                        final String currencyName = balance.getCurrencyName();
                                        double activeBalance = balance.getActiveBalance().doubleValue();
                                        double reservedBalance = balance.getReservedBalance().doubleValue();

                                        RateDto rateDto = ratesMap.getOrDefault(currencyName, RateDto.zeroRate(currencyName));

                                        final double usdRate = rateDto.getUsdRate().doubleValue();

                                        return (activeBalance + reservedBalance) * usdRate;
                                    })
                                    .sum();

                            return sum >= 1;
                        })
                        .count())
                .build();
    }

    @Override
    public void blockUserByRequest(int userId) {
        User user = new User();
        user.setId(userId);
        user.setUserStatus(UserStatus.DELETED);
        userDao.updateUserStatus(user);
        userSessionService.invalidateUserSessionExceptSpecific(getEmailById(userId), null);
    }


    @Override
    public int updateCallbackURL(int userId, CallbackURL callbackUrl) {
        return userSettingService.updateCallbackURL(userId, callbackUrl);
    }

    @Override
    public int setCallbackURL(int userId, CallbackURL callbackUrl) {
        if (!Strings.isNullOrEmpty(userSettingService.getCallbackURL(userId, callbackUrl.getPairId()))) {
            throw new CallBackUrlAlreadyExistException("Callback already present");
        }
        return userSettingService.addCallbackURL(userId, callbackUrl);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TemporalToken verifyUserEmailForForgetPassword(String token) {
        return userDao.verifyToken(token);
    }

    @Override
    public String getUserKycStatusByEmail(String email) {
        return userDao.getKycStatusByEmail(email);
    }

    @Override
    public boolean updateVerificationStatus(String email, String status) {
        return userDao.updateVerificationStatus(email, status);
    }

    @Override
    public boolean updateKycStatus(String status) {
        return userDao.updateVerificationStatus(getUserEmailFromSecurityContext(), status);
    }

    @Override
    public String getKycReferenceByEmail(String email) {
        return userDao.findKycReferenceByUserEmail(email);
    }

    @Override
    public boolean addPolicyToUser(String email, String policy) {
        PolicyEnum policyEnum = PolicyEnum.convert(policy);
        User user = userDao.findByEmail(email);
        if (userDao.existPolicyByUserIdAndPolicy(user.getId(), policy)) {
            return true;
        }
        return userDao.updateUserPolicyByEmail(email, policyEnum);
    }

    @Override
    public boolean updateUserRole(int userId, UserRole userRole) {
        return userDao.updateUserRole(userId, userRole);
    }

    @Override
    public boolean existPolicyByUserIdAndPolicy(int id, String name) {
        return userDao.existPolicyByUserIdAndPolicy(id, name);
    }

    @Override
    public String getEmailByPubId(String pubId) {
        return userDao.getEmailByPubId(pubId);
    }

    @Override
    public String getPubIdByEmail(String email) {
        return userDao.getPubIdByEmail(email);
    }

    @Override
    public DataTable<List<IpLogDto>> getIpAdressesTable(AdminIpLogsFilterData adminOrderFilterData, DataTableParams dataTableParams) {
        PagingData<List<IpLogDto>> searchResult = userDao.getIpLogPage(adminOrderFilterData, dataTableParams);
        DataTable<List<IpLogDto>> output = new DataTable<>();
        output.setData(searchResult.getData());
        output.setRecordsTotal(searchResult.getTotal());
        output.setRecordsFiltered(searchResult.getFiltered());
        return output;
    }

    @Override
    public boolean updateCountryCode(String countryCode) {
        return userDao.updateCountryCode(countryCode, getUserEmailFromSecurityContext());
    }

    @Override
    public void deleteTemporalTokenByUserIdAndTokenType(int userId, TokenType tokenType) {
        userDao.deleteTemporalTokenByUserIdAndTokenType(userId, tokenType);
    }

    @Override
    public boolean subscribeToMailingByPublicId(String publicId, boolean subscribe) {
        return userDao.subscribeToMailingByPublicId(publicId, subscribe);
    }

    @Override
    public boolean subscribeToMailingByEmail(String email, boolean subscribe) {
        return userDao.subscribeToMailingByEmail(email, subscribe);
    }

    @Override
    public void deleteUserPin(String email, NotificationMessageEventEnum login) {
        userPinDao.delete(email, login);
    }

    @Override
    public void updateUserTradeRestrictions(HttpServletRequest request, UserDetails userDetails) {
        boolean isRequired = geoLocationService.isCountryRestrictedByIp(request, RestrictedOperation.TRADE);
        if (! isRequired) {
            return;
        }
        final User user = userDao.findByEmail(userDetails.getUsername());
        if (Objects.nonNull(user) && ! user.hasTradePrivileges()) {
            userDao.setUserVerificationRequired(user.getId(), isRequired);
        }
    }

    @Override
    public List<User> findByInviteReferralLink(String link) {
        return userDao.findByInviteReferralLink(link);
    }

    @Override
    public List<User> findByInviteReferralLink(List<String> links) {
        return userDao.findByInviteReferralLink(links);
    }
}
