package me.exrates.security.service;

import java.util.List;

import me.exrates.dao.UserDao;
import me.exrates.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



public class UserSecureServiceImpl implements UserSecureService {

	@Autowired
	UserDao userDao;

	public List<User> getAllUsers() {
		return userDao.getAllUsers();
		
	}
	
	public List<String> getUserRoles(String email){
		return userDao.getUserRoles(email);
	}
}
