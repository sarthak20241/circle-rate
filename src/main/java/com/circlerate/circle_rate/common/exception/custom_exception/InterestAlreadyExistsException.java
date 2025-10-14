package com.circlerate.circle_rate.common.exception.custom_exception;

public class InterestAlreadyExistsException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InterestAlreadyExistsException(String msg) {
        super(msg);
    }
    
}
