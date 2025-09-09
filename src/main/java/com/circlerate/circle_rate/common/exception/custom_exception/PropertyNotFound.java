package com.circlerate.circle_rate.common.exception.custom_exception;

public class PropertyNotFound extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public PropertyNotFound(String msg) {
        super(msg);
    }
}
