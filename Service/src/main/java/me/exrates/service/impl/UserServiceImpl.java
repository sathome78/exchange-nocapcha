package me.exrates.service.impl;


import me.exrates.dao.UserDao;
import me.exrates.model.Email;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.exception.UnRegisteredUserDeleteException;
import me.exrates.service.token.TokenScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TokenScheduler tokenScheduler;

    private final int USER_FILES_THRESHOLD = 3;

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    @Transactional(rollbackFor = Exception.class)
    public boolean create(User user, Locale locale) {
        LOGGER.info("Begin 'create' method");
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

    /**
     * Verifies user by token that obtained by the redirection from email letter
     * if the verifying is success, all token corresponding type of this user will be deleted
     * if there are jobs for deleted tokens in scheduler, they will be deleted from queue.
     */
    @Transactional(rollbackFor = Exception.class)
    public int verifyUserEmail(String token) {
        LOGGER.info("Begin 'verifyUserEmail' method");
        TemporalToken temporalToken = userDao.verifyToken(token);
        //deleting all tokens related with current through userId and tokenType
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
                if (!userDao.setIpStateConfirmed(temporalToken.getUserId(), temporalToken.getCheckIp())) return 0;
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
        LOGGER.info("Begin 'deleteExpiredToken' method");
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
        LOGGER.info("Begin 'getIdByEmail' method");
        return userDao.getIdByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        LOGGER.info("Begin 'findByEmail' method");
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
    public void deleteUserFile(final int docId) {

        userDao.deleteUserDoc(docId);
    }

    @Override
    public List<UserFile> findUserDoc(final int userId) {
        return userDao.findUserDoc(userId);
    }

    public boolean ifNicknameIsUnique(String nickname) {
        LOGGER.info("Begin 'ifNicknameIsUnique' method");
        return userDao.ifNicknameIsUnique(nickname);
    }

    public boolean ifEmailIsUnique(String email) {
        LOGGER.info("Begin 'ifEmailIsUnique' method");
        return userDao.ifEmailIsUnique(email);
    }

    public String logIP(String email, String host) {
        LOGGER.info("Begin 'logIP' method");
        int id = userDao.getIdByEmail(email);
        String userIP = userDao.getIP(id);
        if (userIP == null) {
            userDao.setIP(id, host);
        }
        userDao.addIPToLog(id, host);
        return userIP;
    }

    private String generateRegistrationToken() {
        LOGGER.info("Begin 'generateRegistrationToken' method");
        return UUID.randomUUID().toString();

    }

    public List<UserRole> getAllRoles() {
        LOGGER.info("Begin 'getAllRoles' method");
        return userDao.getAllRoles();
    }

    public User getUserById(int id) {
        LOGGER.info("Begin 'getUserById' method");
        return userDao.getUserById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createUserByAdmin(User user) {
        LOGGER.info("Begin 'createUserByAdmin' method");
        return userDao.create(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserByAdmin(UpdateUserDto user) {
        LOGGER.info("Begin 'createUserByAdmin' method");
        return userDao.update(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(UpdateUserDto user, boolean resetPassword, Locale locale) {
        LOGGER.info("Begin 'updateUserByAdmin' method");
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
    @Transactional(rollbackFor = Exception.class)
    public void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale) {
        TemporalToken token = new TemporalToken();
        token.setUserId(user.getId());
        token.setValue(generateRegistrationToken());
        token.setTokenType(tokenType);
        token.setCheckIp(user.getIp());

        createTemporalToken(token);

        Email email = new Email();
        String confirmationUrl = tokenLink + "?token=" + token.getValue();
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
    public void updateCommonReferralRoot(final int userId) {
        userDao.updateCommonReferralRoot(userId);
    }

    @Override
    public String getPreferedLang(int userId) {
        return userDao.getPreferredLang(userId);
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
    public List<UserSummaryDto> getUsersSummaryList(String startDate, String endDate) {
        return userDao.getUsersSummaryList(startDate, endDate);
    }

    @PostConstruct
    private void initTokenTriggers() {
        tokenScheduler.initTrigers();
    }

}