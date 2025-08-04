package com.circlerate.circle_rate.common.exception.body;

import lombok.Getter;


@Getter
public class ErrorMessage {
    private int statusCode;
    private String message;
    public ErrorMessage(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
