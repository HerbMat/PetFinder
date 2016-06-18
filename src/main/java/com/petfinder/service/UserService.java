package com.petfinder.service;

import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petfinder.dao.UserRepository;
import com.petfinder.domain.User;
import com.petfinder.exception.EmailExistsException;
import com.petfinder.exception.EmailsDoesNotMatchException;
import com.petfinder.exception.InvalidEmailException;
import com.petfinder.exception.InvalidUserPasswordException;
import com.petfinder.exception.LoginExistsException;
import com.petfinder.exception.PasswordsDoesNotMatchException;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public User getLoggedUser(){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findOneByLogin(login);
	}
	
	public List<User> getAllUsers()
	{
		return (List<User>) this.userRepository.findAll();
	}

    @Transactional
    public void register(String login, String password, String repeatPassword,
            String email)
            throws LoginExistsException, EmailExistsException,
            InvalidEmailException, PasswordsDoesNotMatchException {
        if (isEmailAddressValid(email)) {
            if (verifyLogin(login) && verifyEmail(email)) {
                if (password.equals(repeatPassword)) {
                    String passwordHash = hashPassword(password);
                    User user = new User(login, email, passwordHash);
                    userRepository.save(user);
                } else {
                    throw new PasswordsDoesNotMatchException(
                            "Passwords does not match."
                    );
                }
			}
		}
	}
		
	@Transactional
	public void changeUserData(String currentPassword, String newPassword, String repeatPassword, String newEmail, String repeatEmail) throws PasswordsDoesNotMatchException, EmailExistsException, EmailsDoesNotMatchException, InvalidUserPasswordException{
		if(checkIfUserIsLogged()){
	        String login = SecurityContextHolder.getContext().getAuthentication().getName();
			User user = userRepository.findOneByLogin(login);
			if(this.passwordEncoder.matches(currentPassword, user.getPassword())){
				boolean isPasswordUpdateRequired = passwordNeedsToBeUpdated(newPassword,repeatPassword);
				boolean isEmailUpdateRequired = emailNeedsToBeUpdated(newEmail,repeatEmail);
				
				if(isPasswordUpdateRequired){
					updatePassword(user,newPassword, repeatPassword);
				}
				if(isEmailUpdateRequired){
					updateEmail(user,newEmail, repeatEmail);
				}
				
				if(isPasswordUpdateRequired == true || isEmailUpdateRequired == true){
					userRepository.save(user);
				}
			} else{
				throw new InvalidUserPasswordException("User's password is invalid.");
			}
		}
	}
	
	@Transactional
	public void changeEmailNotificationValue(boolean emailNotification){
		if(checkIfUserIsLogged()){
	        String login = SecurityContextHolder.getContext().getAuthentication().getName();
			User user = userRepository.findOneByLogin(login);
			
			user.setEmailNotification(emailNotification);
			userRepository.save(user);
		}
	}
	
	@Transactional
    public void blockUser(int id)
    {
    	User user = this.userRepository.findOne((long) id);
    	user.setBanned(true);
    	this.userRepository.save(user);
    }
	
	private void updateEmail(User user, String newEmail, String repeatEmail) throws EmailExistsException, EmailsDoesNotMatchException{
		if(newEmail.equals(repeatEmail)){
			if(verifyEmail(newEmail)){
				user.setEmail(newEmail);
			}
		}	else{
			throw new EmailsDoesNotMatchException("Given emails does not match.");
		}
	}
	
	private void updatePassword(User user, String newPassword, String repeatPassword) throws PasswordsDoesNotMatchException{
		if(newPassword.equals(repeatPassword)){
			user.setPassword(hashPassword(newPassword));
		} else{
			throw new PasswordsDoesNotMatchException("Given passwords does not match.");
		}
	}
	
	private boolean passwordNeedsToBeUpdated(String newPassword, String repeatPassword){
		if((newPassword.equals("")||newPassword == null)&&(repeatPassword.equals("")||repeatPassword == null)){
			return false;
		}
		return true;
	}
	
	private boolean emailNeedsToBeUpdated(String newEmail, String repeatEmail){
		if((newEmail.equals("")||newEmail == null)&&(repeatEmail.equals("")||repeatEmail == null)){
			return false;
		}
		return true;
	}
	
	public boolean checkIfUserIsLogged()
	{	
		if(SecurityContextHolder.getContext().getAuthentication() 
		          instanceof AnonymousAuthenticationToken) {
			return false;
		}
		return true;
	}

    public String getLoggedUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            return null;
        } else {
            org.springframework.security.core.userdetails.User loggedUser = (org.springframework.security.core.userdetails.User) principal;
            return loggedUser.getUsername();
        }
    }

    private String hashPassword(String password) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    private boolean verifyLogin(String login) throws LoginExistsException {
        Boolean exists = userRepository.existsByLogin(login);
        if (!exists) {
            return true;
        }
        throw new LoginExistsException(
                String.format("User '%s' already exists.", login)
        );
    }

    private boolean verifyEmail(String email) throws EmailExistsException {
        Boolean exists = userRepository.existsByEmail(email);
        if (!exists) {
            return true;
        }
        throw new EmailExistsException(
                String.format("Email '%s' is already used.", email)
        );
    }

    private boolean isEmailAddressValid(String email) throws InvalidEmailException {
        boolean valid = EmailValidator.getInstance().isValid(email);
        if (valid) {
            return true;
        } else {
            throw new InvalidEmailException(
                    "Given email address is not valid."
            );
        }
    }
}
