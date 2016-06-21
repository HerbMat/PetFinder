package com.petfinder.controller;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class EmailSenderController {

	private final static String username = "p3tfinder@gmail.com";
	private final static String password = "PetFinder123";
	private final static int port = 587;
	private final static String host = "smtp.gmail.com";
	private final static String protocol = "smtp";
	
    public static JavaMailSender getJavaMailSender(){
    	JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setProtocol(protocol);
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);
        
        Properties mailProps = new Properties();
        mailProps.put("mail.smtps.auth", "true");
        mailProps.put("mail.smtp.starttls.enable", "true");
        mailProps.put("mail.smtp.debug", "true");

        sender.setJavaMailProperties(mailProps);
    	return sender;
    }
}
