package com.circlerate.circle_rate.common.exception.custom_exception;

public class PropertyAlreadyExistsException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public PropertyAlreadyExistsException(String msg) {
        super(msg);
    }
}
