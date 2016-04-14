package me.exrates.service.impl;


import me.exrates.dao.UserDao;
import me.exrates.model.Email;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userdao;

    @Autowired
    SendMailService sendMailService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    HttpServletRequest request;

    @Autowired
    TokenScheduler tokenScheduler;

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    @Transactional(rollbackFor = Exception.class)
    public boolean create(User user, Locale locale) {
        LOGGER.info("Begin 'create' method");
        Boolean flag = false;
        if (this.ifEmailIsUnique(user.getEmail())) {
            if (this.ifNicknameIsUnique(user.getNickname())) {
                if (userdao.create(user)) {
                    int user_id = this.getIdByEmail(user.getEmail());
                    user.setId(user_id);
                    sendEmailWithToken(user, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale);
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
    public User verifyUserEmail(String token) {
        LOGGER.info("Begin 'verifyUserEmail' method");
        TemporalToken temporalToken = userdao.verifyToken(token);
        User user = null;
        //deleting all tokens related with current through userId and tokenType
        if (userdao.deleteTemporalTokensOfTokentypeForUser(temporalToken)) {
            //deleting of appropriate jobs
            tokenScheduler.deleteJobsRelatedWithToken(temporalToken);

            user = new User();
            user.setId(temporalToken.getUserId());
            if (temporalToken.getTokenType() == TokenType.REGISTRATION) {
                user.setStatus(UserStatus.ACTIVE);
                userdao.updateUserStatus(user);
            }
        }
        return user;
    }

    /*
    * for checking if there are open tokens of concrete type for the user
    * */
    public List<TemporalToken> getTokenByUserAndType(User user, TokenType tokenType) {
        return userdao.getTokenByUserAndType(user.getId(), tokenType);
    }

    public List<TemporalToken> getAllTokens() {
        return userdao.getAllTokens();
    }

    /*
    * deletes only concrete token
    * */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExpiredToken(String token) throws UnRegisteredUserDeleteException {
        LOGGER.info("Begin 'deleteExpiredToken' method");
        boolean result = false;
        TemporalToken temporalToken = userdao.verifyToken(token);
        result = userdao.deleteTemporalToken(temporalToken);
        if (temporalToken.getTokenType() == TokenType.REGISTRATION) {
            User user = userdao.getUserById(temporalToken.getUserId());
            if (user.getStatus() == UserStatus.REGISTERED) {
                result = userdao.delete(user);
                if (!result) {
                    throw new UnRegisteredUserDeleteException();
                }
            }
        }
        return result;
    }

    public int getIdByEmail(String email) {
        LOGGER.info("Begin 'getIdByEmail' method");
        return userdao.getIdByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        LOGGER.info("Begin 'findByEmail' method");
        return userdao.findByEmail(email);
    }

    public boolean ifNicknameIsUnique(String nickname) {
        LOGGER.info("Begin 'ifNicknameIsUnique' method");
        return userdao.ifNicknameIsUnique(nickname);
    }

    public boolean ifEmailIsUnique(String email) {
        LOGGER.info("Begin 'ifEmailIsUnique' method");
        return userdao.ifEmailIsUnique(email);
    }

    public String logIP(String email, String host) {
        LOGGER.info("Begin 'logIP' method");
        int id = userdao.getIdByEmail(email);
        String userIP = userdao.getIP(id);
        if (userIP == null) {
            userdao.setIP(id, host);
        }
        userdao.addIPToLog(id, host);
        return userIP;
    }

    private String generateRegistrationToken() {
        LOGGER.info("Begin 'generateRegistrationToken' method");
        return UUID.randomUUID().toString();

    }

    public List<UserRole> getAllRoles() {
        LOGGER.info("Begin 'getAllRoles' method");
        return userdao.getAllRoles();
    }

    public User getUserById(int id) {
        LOGGER.info("Begin 'getUserById' method");
        return userdao.getUserById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createUserByAdmin(User user) {
        LOGGER.info("Begin 'createUserByAdmin' method");
        return userdao.create(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserByAdmin(User user) {
        LOGGER.info("Begin 'createUserByAdmin' method");
        return userdao.update(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(User user, boolean changePassword, boolean changeFinPassword, boolean resetPassword, Locale locale) {
        LOGGER.info("Begin 'updateUserByAdmin' method");

        if (changePassword) {
            user.setStatus(UserStatus.REGISTERED);
        }
        if (userdao.update(user)) {
            if (changePassword) {
                sendEmailWithToken(user, TokenType.CHANGE_PASSWORD, "/changePasswordConfirm", "emailsubmitChangePassword.subject", "emailsubmitChangePassword.text", locale);
            } else if (changeFinPassword) {
                sendEmailWithToken(user, TokenType.CHANGE_FIN_PASSWORD, "/changeFinPasswordConfirm", "emailsubmitChangeFinPassword.subject", "emailsubmitChangeFinPassword.text", locale);
            } else {
                sendEmailWithToken(user, TokenType.CHANGE_PASSWORD, "/resetPasswordConfirm", "emailsubmitResetPassword.subject", "emailsubmitResetPassword.text", locale);
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale) {
        TemporalToken token = new TemporalToken();
        token.setUserId(user.getId());
        token.setValue(generateRegistrationToken());
        token.setTokenType(tokenType);

        createTemporalToken(token);

        Email email = new Email();
        String confirmationUrl = tokenLink + "?token=" + token.getValue();
        String rootUrl = request.getScheme() + "://" + request.getServerName() +
                ":" + request.getServerPort();
        email.setMessage(
                messageSource.getMessage(emailText, null, locale) +
                        " <a href='" +
                        rootUrl +
                        confirmationUrl +
                        "'>"+messageSource.getMessage("admin.ref", null, locale)+"</a>"
        );
        email.setSubject(messageSource.getMessage(emailSubject, null, locale));

        email.setTo(user.getEmail());
        sendMailService.sendMail(email);
    }

    public boolean createTemporalToken(TemporalToken token) {
        boolean result = userdao.createTemporalToken(token);
        if (result) {
            tokenScheduler.initTrigers();
        }
        return result;
    }

    @PostConstruct
    private void initTokenTriggers() {
        tokenScheduler.initTrigers();
    }

}