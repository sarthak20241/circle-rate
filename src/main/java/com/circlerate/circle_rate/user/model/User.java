package com.circlerate.circle_rate.user.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;


@Data
@Document("users")
@Component
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private UserProfile userProfile;
}
