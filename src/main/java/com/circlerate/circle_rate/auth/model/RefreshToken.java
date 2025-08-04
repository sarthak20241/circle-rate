package com.circlerate.circle_rate.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Document("refresh_tokens")
@Component
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String id;
    private String email;
    private String token;
    private Date issuedAt;
    private Date expiresAt;

    public RefreshToken(String email, Date issuedAt, Date expiresAt){
        this.email = email;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

}
