package com.circlerate.circle_rate.auth.payload;

import com.circlerate.circle_rate.auth.model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private Role role;
}
