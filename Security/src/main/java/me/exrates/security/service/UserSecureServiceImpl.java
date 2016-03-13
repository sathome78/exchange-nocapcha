package me.exrates.security.service;

import java.util.List;

import me.exrates.dao.UserDao;
import me.exrates.model.User;

import me.exrates.model.enums.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserSecureServiceImpl implements UserSecureService {

	@Autowired
	UserDao userDao;

	private static final Logger logger = LogManager.getLogger(UserSecureServiceImpl.class);


	public List<User> getAllUsers() {
		logger.info("Begin 'getAllUsers' method");
		return userDao.getAllUsers();
		
	}

	public List<User> getUsersByRoles(List<UserRole> listRoles){
		logger.info("Begin 'getUsersByRoles' method");
		return  userDao.getUsersByRoles(listRoles);
	}

	public UserRole getUserRoles(String email){
		logger.info("Begin 'getUserRoles' method");
		return userDao.getUserRoles(email);
	}
}
