package com.petfinder.rest.domain;

import java.util.List;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.petfinder.controller.EmailSenderController;
import com.petfinder.domain.Advertisement;
import com.petfinder.domain.User;

public class EmailSender implements Runnable{

	private List<User> users;
	private Advertisement advertisement;
	
	public EmailSender(List<User> users, Advertisement advertisement){
		this.users=users;
		this.advertisement = advertisement;
	}
	
	@Override
	public void run() {
		JavaMailSender sender = EmailSenderController.getJavaMailSender();
    	SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("PetFinder");

		for(int i=0;i<users.size();i++){
			message.setTo(users.get(i).getEmail());
			message.setSubject("New advertisment has been added!");
			message.setText("New advertisment from user "+
					advertisement.getUser().getLogin() +
					" has been added. \n\n"+
					advertisement.getContent()
					);
			sender.send(message);	
		}		
	}

}
