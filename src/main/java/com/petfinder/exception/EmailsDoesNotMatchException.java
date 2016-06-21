package com.petfinder.exception;

public class EmailsDoesNotMatchException extends Exception{

    public EmailsDoesNotMatchException() {
    }

    public EmailsDoesNotMatchException(String message) {
        super(message);
    }

    public EmailsDoesNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailsDoesNotMatchException(Throwable cause) {
        super(cause);
    }

    public EmailsDoesNotMatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
