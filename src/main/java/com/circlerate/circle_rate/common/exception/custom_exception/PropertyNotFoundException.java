package com.circlerate.circle_rate.common.exception.custom_exception;

public class PropertyNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public PropertyNotFoundException(String msg) {
        super(msg);
    }
}
