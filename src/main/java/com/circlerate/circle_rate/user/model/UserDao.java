package com.circlerate.circle_rate.user.model;

import com.circlerate.circle_rate.auth.model.LoginType;
import com.circlerate.circle_rate.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Date;


@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UserDao {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private LoginType loginType;
    private Role role;
    private Date createdAt;
    private UserProfile userProfile;
    private String contactNo;
    private String aboutMe;

    public UserDao(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.loginType = user.getLoginType();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.userProfile = user.getUserProfile();
        this.contactNo = user.getContactNo();
        this.aboutMe = user.getAboutMe();
    }
}
