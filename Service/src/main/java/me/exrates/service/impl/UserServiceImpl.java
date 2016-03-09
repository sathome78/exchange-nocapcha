package me.exrates.service.impl;
 

import me.exrates.dao.UserDao;
import me.exrates.model.User;
import me.exrates.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
  
 	@Autowired
 	UserDao userdao;

 	@Transactional
 	public boolean create(User user) {
 		if(this.ifEmailIsUnique(user.getEmail())) {
 			if(this.ifNicknameIsUnique(user.getNickname())) {
 			 return	userdao.create(user);
 			}
 			else return false;
 		}
 		else return false;
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
}