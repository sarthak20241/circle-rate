package com.circlerate.circle_rate.auth.payload;

import com.circlerate.circle_rate.auth.model.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    @NotEmpty
    @Size(min = 8, message = "password should have at least 8 characters")
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
}
