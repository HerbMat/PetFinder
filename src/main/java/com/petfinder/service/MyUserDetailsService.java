package com.petfinder.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petfinder.dao.UserRepository;

@Service("userDetailsService")
public class MyUserDetailsService implements UserDetailsService {

	private final static Logger LOGGER = Logger.getLogger(MyUserDetailsService.class.getName());
	//get user from the database, via Hibernate
	@Autowired
	private UserRepository userDao;

	@Transactional(readOnly=true)
	@Override
	public UserDetails loadUserByUsername(final String username) 
		throws UsernameNotFoundException {
		LOGGER.log(Level.SEVERE,username);
		com.petfinder.domain.User  user = userDao.findOneByLogin(username);
		if(user.isBanned()) {
			return null;
		}
		List<GrantedAuthority> authorities = buildUserAuthority();
		LOGGER.log(Level.SEVERE,user.getLogin());
		return buildUserForAuthentication(user, authorities);
		
	}

	// Converts com.mkyong.users.model.User user to
	// org.springframework.security.core.userdetails.User
	private User buildUserForAuthentication(com.petfinder.domain.User user, 
		List<GrantedAuthority> authorities) {
		return new User(user.getLogin(), user.getPassword(), 
			true, true, true, true, authorities);
	}

	private List<GrantedAuthority> buildUserAuthority() {

		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

		setAuths.add(new SimpleGrantedAuthority("ROLE_USER"));

		List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);

		return Result;
	}

}
