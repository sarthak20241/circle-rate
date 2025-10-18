package com.circlerate.circle_rate.common.exception.custom_exception;
public class UserNotAuthorizedException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public UserNotAuthorizedException(String msg) {
        super(msg);
    }
    
}