package com.circlerate.circle_rate.auth.model;

import lombok.Data;

import java.util.Date;

@Data
public class AccessToken {
    private String email;
    private String token;
    private Role role;
    private Date issuedAt;
    private Date expiresAt;

    public AccessToken(String email, Role role, String token, Date issuedAt, Date expiresAt){
        this.email = email;
        this.token = token;
        this.role = role;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }
}
