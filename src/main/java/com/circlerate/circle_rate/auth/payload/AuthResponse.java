package com.circlerate.circle_rate.auth.payload;

import lombok.Data;

@Data
public class AuthResponse {
    private String userId;
    private String email;
    private String message;
    private String accessToken;
    public AuthResponse(String email, String message){
        this.email=email;
        this.message=message;
    }

    public AuthResponse(String userId, String email, String message, String accessToken){
        this.userId = userId;
        this.email=email;
        this.message=message;
        this.accessToken=accessToken;
    }
}
