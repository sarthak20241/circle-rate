package com.circlerate.circle_rate.common.exception.custom_exception;

public class InterestNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InterestNotFoundException(String msg) {
        super(msg);
    }
    
}
