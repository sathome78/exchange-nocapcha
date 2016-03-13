package me.exrates.security.service;

import me.exrates.model.User;
import me.exrates.model.enums.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserSecureService userSecureService;

	private static final Logger logger = LogManager.getLogger(UserDetailsServiceImpl.class);

    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		logger.info("Begin 'loadUserByUsername' method");
		org.springframework.security.core.userdetails.User userSpring;
		List<User> listUser = userSecureService.getAllUsers(); 
		User person = getUser(listUser, login);
		if (person == null)
		{
			throw new UsernameNotFoundException("Несуществующий логин");
		} else
		{
			userSpring = new org.springframework.security.core.userdetails.User(person.getEmail(), person.getPassword(), ifUserAllowed(person), true, true, true,
					getAuthorities(person.getEmail()));
		}
		return userSpring;
   }
    private User getUser(List<User> lp, String userName)
	{
		logger.info("Begin 'getUser' method");
		User pers = null;
		for (User p : lp)
		{
			if (userName.equals(p.getEmail()))
			{
				pers = p;
			}
		}
		return pers;
	}
      
    private Collection<GrantedAuthority> getAuthorities(String login)
	{
		logger.info("Begin 'getAuthorities' method");
		UserRole role = userSecureService.getUserRoles(login);
    	Collection<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
    	authList.add(new SimpleGrantedAuthority(role.name()));

		return authList;
	}
    
    private boolean ifUserAllowed(User user) {
		logger.info("Begin 'ifUserAllowed' method");
		if(user.getStatus().getStatus()==2) {
    		return true;
    	}
    	return false;
    }

	}