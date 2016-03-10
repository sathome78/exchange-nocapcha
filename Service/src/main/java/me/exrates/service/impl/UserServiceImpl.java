package me.exrates.service.impl;


import java.util.Locale;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import me.exrates.dao.UserDao;
import me.exrates.model.Email;
import me.exrates.model.RegistrationToken;
import me.exrates.model.User;
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
 					String rootUrl = request.getLocalAddr();
 					email.setMessage(
 							messageSource.getMessage("emailsubmitregister.text", null, ru)+
 							" <a href='"+
 							rootUrl+
 							confirmationUrl+
 							"'>Link</a>"
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
 		System.out.println("token = "+token);
 		RegistrationToken rt = userdao.verifyToken(token);
		userdao.deleteRegistrationToken(rt);
		User user = new User();
		user.setId(rt.getUserId());
		user.setStatus(UserStatus.ACTIVE);
		userdao.updateUserStatus(user);
 	}
 	
 	public int getIdByEmail(String email) {
 		return userdao.getIdByEmail(email);  
 	}

	@Override
	public User findByEmail(String email) {
		return userdao.findByEmail(email);
	}

	public boolean ifNicknameIsUnique(String nickname) {
	return userdao.ifNicknameIsUnique(nickname);	
}  
  
	public boolean ifEmailIsUnique(String email) {
	return userdao.ifEmailIsUnique(email);	
}

	public String logIP(String email, String host) {
		int id = userdao.getIdByEmail(email);
		String userIP = userdao.getIP(id);
		if(userIP == null) {
			userdao.setIP(id, host);
		}
		userdao.addIPToLog(id, host);
		return userIP;
	}
	
	private String generateRegistrationToken() {
		return UUID.randomUUID().toString();
	
	}
}