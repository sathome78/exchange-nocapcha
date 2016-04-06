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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
//	private static final Locale ru = new Locale("ru");
 	
 	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
 	
 	@Transactional(rollbackFor=Exception.class)
 	public boolean create(User user, Locale locale) {
		logger.info("Begin 'create' method");
		Boolean flag = false;
 		if(this.ifEmailIsUnique(user.getEmail())) {
 			if(this.ifNicknameIsUnique(user.getNickname())) {
 				if(userdao.create(user)) {
 					int user_id = this.getIdByEmail(user.getEmail());
					user.setId(user_id);
					sendEmailWithToken(user, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale);
 				}
 			}
 			
 		}
 		return flag;
 	}  
 
 	@Transactional(rollbackFor=Exception.class)
 	public User verifyUserEmail(String token) {
		logger.info("Begin 'verifyUserEmail' method");
 		TemporalToken temporalToken = userdao.verifyToken(token);
		userdao.deleteTemporalToken(temporalToken);
		User user = new User();
		user.setId(temporalToken.getUserId());
		user.setStatus(UserStatus.ACTIVE);
		userdao.updateUserStatus(user);
		return user;
 	}

	/*
	* this method verify if there is token with concrete TokenType in db
	* and delete ALL tokens of this TokenType for user that holds this token
	* It's used for clear user tokens if fin password is confirmed by email
	* */
	@Transactional(rollbackFor=Exception.class)
	public void verifyUserEmail(String token, TokenType tokenType) {
		logger.info("Begin 'verifyUserEmail' method");
		TemporalToken temporalToken = userdao.verifyToken(token);
		if (temporalToken != null) {
			userdao.deleteTemporalToken(temporalToken.getUserId(), tokenType);
		}
	}

	public List<TemporalToken> getTokenByUserAndType(User user, TokenType tokenType){
		return userdao.getTokenByUserAndType(user.getId(), tokenType);
	}
 	
 	public int getIdByEmail(String email) {
		logger.info("Begin 'getIdByEmail' method");
 		return userdao.getIdByEmail(email);
 	}

	@Override
	public User findByEmail(String email) {
		logger.info("Begin 'findByEmail' method");
		return userdao.findByEmail(email);
	}

	public boolean ifNicknameIsUnique(String nickname) {
		logger.info("Begin 'ifNicknameIsUnique' method");
		return userdao.ifNicknameIsUnique(nickname);
}  
  
	public boolean ifEmailIsUnique(String email) {
		logger.info("Begin 'ifEmailIsUnique' method");
		return userdao.ifEmailIsUnique(email);
}

	public String logIP(String email, String host) {
		logger.info("Begin 'logIP' method");
		int id = userdao.getIdByEmail(email);
		String userIP = userdao.getIP(id);
		if(userIP == null) {
			userdao.setIP(id, host);
		}
		userdao.addIPToLog(id, host);
		return userIP;
	}
	
	private String generateRegistrationToken() {
		logger.info("Begin 'generateRegistrationToken' method");
		return UUID.randomUUID().toString();
	
	}

    public 	List<UserRole> getAllRoles(){
		logger.info("Begin 'getAllRoles' method");
		return userdao.getAllRoles();
	}

	public 	User getUserById(int id){
		logger.info("Begin 'getUserById' method");
		return userdao.getUserById(id);
	}

	@Transactional(rollbackFor=Exception.class)
	public boolean createUserByAdmin(User user){
		logger.info("Begin 'createUserByAdmin' method");
		return userdao.create(user);
	}

	@Transactional(rollbackFor=Exception.class)
	public boolean updateUserByAdmin(User user){
		logger.info("Begin 'createUserByAdmin' method");
		return userdao.update(user);
	}

	@Transactional(rollbackFor=Exception.class)
	public boolean update(User user, boolean changePassword, boolean changeFinPassword, boolean resetPassword, Locale locale){
		logger.info("Begin 'updateUserByAdmin' method");

		if (changePassword){
			user.setStatus(UserStatus.REGISTERED);
		}
		if(userdao.update(user)) {
			if (changePassword) {
				sendEmailWithToken(user, TokenType.CHANGE_PASSWORD, "/changePasswordConfirm", "emailsubmitChangePassword.subject", "emailsubmitChangePassword.text", locale);
			}else if (changeFinPassword){
				sendEmailWithToken(user, TokenType.CHANGE_FIN_PASSWORD, "/changeFinPasswordConfirm", "emailsubmitChangeFinPassword.subject", "emailsubmitChangeFinPassword.text", locale);
			}else {
				sendEmailWithToken(user, TokenType.CHANGE_PASSWORD, "/resetPasswordConfirm", "emailsubmitResetPassword.subject", "emailsubmitResetPassword.text", locale);
			}
		}
		return true;
	}

	@Transactional(rollbackFor=Exception.class)
	public void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale) {
		TemporalToken token = new TemporalToken();
		token.setUserId(user.getId());
		token.setValue(generateRegistrationToken());
		token.setTokenType(tokenType);
		userdao.createTemporalToken(token);

		Email email = new Email();
		String confirmationUrl = tokenLink + "?token=" + token.getValue();
		String rootUrl = request.getScheme() +"://"+ request.getServerName() +
				":" + request.getServerPort();
		email.setMessage(
				messageSource.getMessage(emailText, null, locale)+
						" <a href='"+
						rootUrl+
						confirmationUrl+
						"'>Ссылка</a>"
		);
		email.setSubject(messageSource.getMessage(emailSubject, null, locale));

		email.setTo(user.getEmail());
		sendMailService.sendMail(email);
	}

}