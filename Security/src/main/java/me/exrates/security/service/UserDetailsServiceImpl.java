package me.exrates.security.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.exrates.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserSecureService userSecureService;
    
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        
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
		List<String> roleList = userSecureService.getUserRoles(login);
    	Collection<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
		for(String role: roleList) {
	    	authList.add(new SimpleGrantedAuthority(role));	
		}
		return authList;
	}
    
    private boolean ifUserAllowed(User user) {
    	if(user.getStatus().getStatus()==2 || user.getStatus().getStatus()==1) {
    		return true;
    	}
    	return false;
    }

	}