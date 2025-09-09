package com.circlerate.circle_rate.common.exception.custom_exception;

public class PropertyAlreadyExists extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public PropertyAlreadyExists(String msg) {
        super(msg);
    }
}
