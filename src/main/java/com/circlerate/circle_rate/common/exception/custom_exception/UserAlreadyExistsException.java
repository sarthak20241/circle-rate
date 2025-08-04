package com.circlerate.circle_rate.common.exception.custom_exception;

public class UserAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
