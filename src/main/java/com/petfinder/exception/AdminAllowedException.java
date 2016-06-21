package com.petfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class AdminAllowedException extends RuntimeException {

	public AdminAllowedException(String string) {
		super(string);
	}
	
}