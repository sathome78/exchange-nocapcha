package me.exrates.service.impl;


import java.util.Locale;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import me.exrates.dao.UserDao;
import me.exrates.model.Email;
import me.exrates.model.RegistrationToken;
import me.exrates.model.User;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
	
	private static final Locale ru = new Locale("ru");
 	
 	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
 	
 	@Transactional(rollbackFor=Exception.class)
 	public boolean create(User user) {
		logger.info("Begin 'create' method");
		Boolean flag = false;
 		if(this.ifEmailIsUnique(user.getEmail())) {
 			if(this.ifNicknameIsUnique(user.getNickname())) {
 				if(userdao.create(user)) {
 					int user_id = this.getIdByEmail(user.getEmail());
 	
 					RegistrationToken token = new RegistrationToken();
 					token.setUserId(user_id);
 					token.setValue(generateRegistrationToken());
 					userdao.createRegistrationToken(token);
 					
 					Email email = new Email();
 					String confirmationUrl = "/registrationConfirm?token=" + token.getValue();
 					String rootUrl = request.getScheme() +"://"+ request.getServerName();
					email.setMessage(
 							messageSource.getMessage("emailsubmitregister.text", null, ru)+
 							" <a href='"+
 							rootUrl+
 							confirmationUrl+
 							"'>Ссылка</a>"
 							);
 					email.setSubject(messageSource.getMessage("emailsubmitregister.subject", null, ru));
 				
 					email.setTo(user.getEmail());
					sendMailService.sendMail(email);
 				}
 			}
 			
 		}
 		return flag;
 	}  
 
 	@Transactional(rollbackFor=Exception.class)
 	public void verifyUserEmail(String token) {
		logger.info("Begin 'verifyUserEmail' method");
 		RegistrationToken rt = userdao.verifyToken(token);
		userdao.deleteRegistrationToken(rt);
		User user = new User();
		user.setId(rt.getUserId());
		user.setStatus(UserStatus.ACTIVE);
		userdao.updateUserStatus(user);
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
		logger.info("Begin 'updateUserByAdmin' method");
		return userdao.update(user);
	}

}