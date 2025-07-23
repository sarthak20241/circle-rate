package com.circlerate.circle_rate.user.model;

import com.circlerate.circle_rate.auth.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Data
@Document("approval_requests")
@Component
@NoArgsConstructor
public class Approval {
    @Id
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;

    public Approval(User user, Role role){
        this.email=user.getEmail();
        this.firstName=user.getFirstName();
        this.lastName=user.getLastName();
        this.role=role;
    }
}
