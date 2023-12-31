package me.exrates.security.service.impl;

import com.google.common.base.Preconditions;
import me.exrates.dao.UserDao;
import me.exrates.model.Email;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserEmailDto;
import me.exrates.model.dto.ErrorReportDto;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserEventEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngModel.PasswordCreateDto;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.service.AuthTokenService;
import me.exrates.security.service.NgUserService;
import me.exrates.service.SendMailService;
import me.exrates.service.TemporalTokenService;
import me.exrates.service.UserService;
import me.exrates.service.userOperation.UserOperationService;
import me.exrates.service.util.IpUtils;
import me.exrates.service.util.RestApiUtilComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import static me.exrates.service.util.RestUtil.getUrlFromRequest;

@Service
@PropertySource(value = {"classpath:/angular.properties"})
public class NgUserServiceImpl implements NgUserService {

    private static final Logger logger = LogManager.getLogger(NgUserServiceImpl.class);
    private final UserDao userDao;
    private final UserService userService;
    private final MessageSource messageSource;
    private final SendMailService sendMailService;
    private final AuthTokenService authTokenService;
    private final IpBlockingService ipBlockingService;
    private final TemporalTokenService temporalTokenService;
    private final HttpServletRequest request;
    private final RestApiUtilComponent restApiUtilComponent;
    private final UserOperationService userOperationService;

    @Value("${dev.mode}")
    private boolean DEV_MODE;

    @Value("${front-host}")
    private String host;
    @Value("${mandrill.email}")
    private String mandrillEmail;
    @Value("${support.email}")
    private String supportEmail;

    @Autowired
    public NgUserServiceImpl(UserDao userDao,
                             UserService userService,
                             MessageSource messageSource,
                             SendMailService sendMailService,
                             AuthTokenService authTokenService,
                             IpBlockingService ipBlockingService,
                             TemporalTokenService temporalTokenService,
                             HttpServletRequest request,
                             RestApiUtilComponent restApiUtilComponent, UserOperationService userOperationService) {
        this.userDao = userDao;
        this.userService = userService;
        this.messageSource = messageSource;
        this.sendMailService = sendMailService;
        this.authTokenService = authTokenService;
        this.ipBlockingService = ipBlockingService;
        this.temporalTokenService = temporalTokenService;
        this.request = request;
        this.restApiUtilComponent = restApiUtilComponent;
        this.userOperationService = userOperationService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean registerUser(UserEmailDto userEmailDto, HttpServletRequest request) {

        if (!userService.ifEmailIsUnique(userEmailDto.getEmail())) {
            throw new NgDashboardException("email is exist or banned");
        }
        User user = new User();
        user.setEmail(userEmailDto.getEmail());
        user.setIp(IpUtils.getClientIpAddress(request));
        user.setVerificationRequired(userEmailDto.getIsUsa());
        if (StringUtils.isNoneEmpty(userEmailDto.getInviteCode()))
            user.setInviteReferralLink(userEmailDto.getInviteCode());

        if (!(userDao.create(user) && userDao.insertIp(user.getEmail(), user.getIp()))) {
            return false;
        }

        int idUser = userDao.getIdByEmail(userEmailDto.getEmail());
        user.setPublicId(userDao.getPubIdByEmail(userEmailDto.getEmail()));
        user.setId(idUser);

        userService.logIP(idUser, user.getIp(), UserEventEnum.REGISTER, getUrlFromRequest(request));
        sendEmailWithToken(user,
                TokenType.REGISTRATION,
                "emailsubmitregister.subject",
                "emailsubmitregister.text",
                Locale.ENGLISH, getHost(),
                "final-registration/token?t=",
                userEmailDto.getIsUsa()
        );


        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuthTokenDto createPassword(PasswordCreateDto passwordCreateDto, HttpServletRequest request) {
        String tempToken = passwordCreateDto.getTempToken();
        User user = userService.getUserByTemporalToken(tempToken);
        if (user == null) {
            logger.error("Error create password for user, temp_token {}", tempToken);
            throw new NgDashboardException("User not found", 1001);
        }

        String password = restApiUtilComponent.decodePassword(passwordCreateDto.getPassword());
        user.setUserStatus(UserStatus.ACTIVE);
        UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setPassword(password);
        updateUserDto.setStatus(UserStatus.ACTIVE);
        updateUserDto.setRole(UserRole.USER);

        boolean update = userService.updateUserSettings(updateUserDto);
        if (update) {
            Optional<AuthTokenDto> authTokenResult = authTokenService.retrieveTokenNg(user.getEmail());
            AuthTokenDto authTokenDto =
                    null;
            try {
                authTokenDto = authTokenResult.orElseThrow(() ->
                        new NgDashboardException("Failed to authenticate user with email: " + user.getEmail(), 1002));
            } catch (Exception e) {
                logger.error("Error creating token with email {}", user.getEmail());
            }
//
//            authTokenDto.setReferralReference(referralService.generateReferral(user.getEmail()));
//            ipBlockingService.successfulProcessing(IpUtils.getClientIpAddress(request), IpTypesOfChecking.LOGIN);
            userService.deleteTempTokenByValue(tempToken);

            sendWelcomeMail(user.getEmail());

            return authTokenDto;
        } else {
            logger.error("Update fail, user id - {}, email - {}", user.getId(), user.getEmail());
            throw new NgDashboardException("Error while creating password");
        }
    }

    private void sendWelcomeMail(String emailAddress) {
        String message;
        try {
            File file = ResourceUtils.getFile("classpath:email/welcome_letter.txt");


            BufferedReader reader = Files.newBufferedReader(file.toPath());

            StringBuilder builder = new StringBuilder();
            reader.lines().forEach(line -> {
                builder.append(line);
                builder.append("\n");
            });
            message = builder.toString();
        } catch (IOException ex) {
            logger.error("Email text not found", ex);
            return;
        }
        Email email = new Email();
        email.setTo(emailAddress);
        email.setSubject("New user registration");
        email.setMessage(message);

        Properties properties = new Properties();
        properties.setProperty("public_id", userService.getPubIdByEmail(emailAddress));
        email.setProperties(properties);

        sendMailService.sendMail(email);
    }

    @Override
    public boolean recoveryPassword(UserEmailDto userEmailDto, HttpServletRequest request) {

        String emailIncome = userEmailDto.getEmail();
        User user = userDao.findByEmail(emailIncome);
        userService.deleteTemporalTokenByUserIdAndTokenType(user.getId(), TokenType.CHANGE_PASSWORD);
        sendEmailWithToken(user,
                TokenType.CHANGE_PASSWORD,
                "emailsubmitResetPassword.subject",
                "emailsubmitResetPassword.text",
                Locale.ENGLISH, getHost(),
                "recovery-password?t=",
                false);

        return true;
    }

    @Override
    public boolean createPasswordRecovery(PasswordCreateDto passwordCreateDto, HttpServletRequest request) {
        String tempToken = passwordCreateDto.getTempToken();
        User user = userService.getUserByTemporalToken(tempToken);
        if (user == null) {
            logger.error("Error create recovery password for user, temp_token {}", tempToken);
            return false;
        }

        String password = restApiUtilComponent.decodePassword(passwordCreateDto.getPassword());
        UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setPassword(password);
        updateUserDto.setStatus(user.getUserStatus());
        updateUserDto.setRole(user.getRole());

        return userService.updateUserSettings(updateUserDto);
    }

    @Override
    public boolean validateTempToken(String token) {

        TemporalToken temporalToken = userService.getTemporalTokenByValue(token);

        if (temporalToken == null || temporalToken.isAlreadyUsed()) return false;

        return temporalTokenService.updateTemporalToken(temporalToken);
    }

    @Override
    public void sendEmailDisable2Fa(String userEmail) {
        Email email = new Email();
        email.setTo(userEmail);
        email.setMessage("Dear user,\n" +
                "\n" +
                "We received a successful request to disable Two-Factor Authentication on your account with Google Authenticator.\n" +
                "\n" +
                "If this was not you, please change your password and contact us.");
        email.setSubject("Notification of disable 2FA");

        Properties properties = new Properties();
        properties.setProperty("public_id", userService.getPubIdByEmail(userEmail));
        email.setProperties(properties);

        sendMailService.sendMail(email);
    }

    @Override
    public void sendEmailEnable2Fa(String userEmail) {
        Email email = new Email();
        email.setTo(userEmail);
        email.setMessage("Dear user,\n" +
                "\n" +
                "We received a successful request to enable Two-Factor Authentication on your account with Google Authenticator.\n" +
                "\n" +
                "If this was not you, please change your password and contact us.");
        email.setSubject("Notification of enable 2FA");

        Properties properties = new Properties();
        properties.setProperty("public_id", userService.getPubIdByEmail(userEmail));
        email.setProperties(properties);

        sendMailService.sendMail(email);
    }

    @Override
    public void resendEmailForFinishRegistration(User user) {
        List<TemporalToken> tokens = userService.getTokenByUserAndType(user, TokenType.REGISTRATION);
        tokens.forEach(o -> userService.deleteTempTokenByValue(o.getValue()));

        sendEmailWithToken(user,
                TokenType.REGISTRATION,
                "emailsubmitregister.subject",
                "emailsubmitregister.text",
                Locale.ENGLISH, getHost(), "final-registration/token?t=",
                user.getVerificationRequired());

    }

    @Transactional(readOnly = true)
    @Override
    public String getUserPublicId() {
        final String userEmail = userService.getUserEmailFromSecurityContext();

        return userDao.getUserPublicId(userEmail);
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendEmailWithToken(User user,
                                   TokenType tokenType,
                                   String emailSubject,
                                   String emailText,
                                   Locale locale,
                                   String host,
                                   String confirmationUrl,
                                   Boolean needVerification) {
        TemporalToken token = new TemporalToken();
        token.setUserId(user.getId());
        token.setValue(UUID.randomUUID().toString());
        token.setTokenType(tokenType);
        token.setCheckIp(user.getIp());
        token.setAlreadyUsed(false);
        logger.info("sendEmailWithToken(), temp-token {}, email {}", token.getValue(), user.getEmail());

        userService.createTemporalToken(token);

        Email email = new Email();

        confirmationUrl = confirmationUrl + token.getValue() + "&needVerification=" + needVerification;

        email.setMessage(
                messageSource.getMessage(emailText, null, locale) +
                        " </p><a href=\"" +
                        host + "/" + confirmationUrl +
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
    public void sendErrorReportEmail(ErrorReportDto dto) {
        Preconditions.checkNotNull(dto);

        sendMailService.sendMailMandrill(Email.builder()
                .from(mandrillEmail)
                .to(supportEmail)
                .message(dto.toString())
                .subject("Error report")
                .properties(new Properties())
                .build());
    }

    private String getHost() {
        return host;
    }

}
