package com.circlerate.circle_rate.auth.payload;

import com.circlerate.circle_rate.auth.model.LoginType;
import com.circlerate.circle_rate.auth.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message = "Email is required")
    private String email;
    private String password;
    @NotBlank(message = "First Name is required")
    private String firstName;
    private String lastName;
    @NotNull(message = "Role must not be null")
    private Role role;
    private LoginType loginType;

    public boolean checkValidPassword(){
        if(password!=null){
            if(password.equals("google-signup")){
                return true;
            }
            else return password.length() >= 8;
        }
        return false;
    }
}
