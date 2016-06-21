package com.petfinder.exception;

public class NoUsersToNotifyException extends Exception{

	public NoUsersToNotifyException(){
		super();
	}
	
	public NoUsersToNotifyException(String message) {
		super(message);
	}

	public NoUsersToNotifyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoUsersToNotifyException(Throwable cause) {
		super(cause);
	}

	public NoUsersToNotifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
