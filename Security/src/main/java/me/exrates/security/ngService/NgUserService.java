package me.exrates.security.ngService;

import me.exrates.model.User;
import me.exrates.model.UserEmailDto;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

public interface NgUserService {
    @Transactional(rollbackFor = Exception.class)
    boolean registerUser(UserEmailDto userEmailDto, HttpServletRequest request);

    @Transactional(rollbackFor = Exception.class)
    AuthTokenDto createPassword(PasswordCreateDto passwordCreateDto, HttpServletRequest request);

    boolean recoveryPassword(UserEmailDto userEmailDto, HttpServletRequest request);

    boolean createPasswordRecovery(PasswordCreateDto passwordCreateDto, HttpServletRequest request);

    boolean validateTempToken(String token);

    void sendEmailDisable2Fa(String userEmail);

    void sendEmailEnable2Fa(String userEmail);

    void resendEmailForFinishRegistration(User user);
}
