package com.circlerate.circle_rate.user.model;

import com.circlerate.circle_rate.auth.model.LoginType;
import com.circlerate.circle_rate.auth.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;


@Data
@Document("users")
@Component
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private String firstName;
    private String lastName;
    private String encodedPassword;
    private LoginType loginType;
    private Role role;
    private Date createdAt;
    private UserProfile userProfile;

    public User(String email, String encodedPassword, String firstName, String lastName, LoginType loginType){
        this.email=email;
        this.encodedPassword = encodedPassword;
        this.firstName=firstName;
        this.lastName=lastName;
        this.createdAt = new Date();
        this.loginType = loginType;
    }

    public boolean isUserRoleAllowed(Role role) {
        return this.role == role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.encodedPassword;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
