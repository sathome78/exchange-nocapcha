package me.exrates.security.service;

import java.util.List;

import me.exrates.dao.UserDao;
import me.exrates.model.User;

import me.exrates.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserSecureServiceImpl implements UserSecureService {

	@Autowired
	UserDao userDao;

	public List<User> getAllUsers() {
		return userDao.getAllUsers();
		
	}

	public List<User> getUsersByRoles(List<UserRole> listRoles){
		return  userDao.getUsersByRoles(listRoles);
	}

	public UserRole getUserRoles(String email){
		return userDao.getUserRoles(email);
	}
}
