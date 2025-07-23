package com.circlerate.circle_rate.user.service;

import com.circlerate.circle_rate.auth.model.AccessToken;
import com.circlerate.circle_rate.auth.model.RefreshToken;
import com.circlerate.circle_rate.auth.model.Role;
import com.circlerate.circle_rate.auth.payload.AuthResponse;
import com.circlerate.circle_rate.auth.payload.LoginRequest;
import com.circlerate.circle_rate.auth.payload.SignupRequest;
import com.circlerate.circle_rate.auth.repository.RefreshTokenRepository;
import com.circlerate.circle_rate.auth.service.JwtService;
import com.circlerate.circle_rate.common.constants.ResponseMessage;
import com.circlerate.circle_rate.user.model.User;
import com.circlerate.circle_rate.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ApprovalService approvalService;
    @Autowired
    JwtService jwtService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    private final boolean isLocal = false;

    public ResponseEntity<AuthResponse> signup(SignupRequest request, HttpServletResponse response) {
        if(userRepository.existsByEmail(request.getEmail())){
            log.info("User already exists with email : {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthResponse(request.getEmail(),ResponseMessage.USER_ALREADY_EXISTS));
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getEmail(),encodedPassword,request.getFirstName(),request.getLastName());
        if(request.getRole()!= Role.USER){
            log.info("Approval Raised for user: {} for role: {}", request.getEmail(), request.getRole());
            approvalService.requestApproval(user,request.getRole());
            user.setRole(Role.TEMP_ROLE);
        }
        else{
            user.setRole(request.getRole());
        }
        User savedUser = userRepository.save(user);
        AccessToken accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());
        RefreshToken refreshToken = jwtService.generateRefreshToken(user.getEmail());
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(!isLocal) // Set to false in local dev if not using HTTPS
                .path("/auth")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        if((user.getRole() != request.getRole())){
            log.info("user created without role: {} permissions", request.getRole() );
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(savedUser.getId(), savedUser.getEmail(), ResponseMessage.APPROVAL_RAISED, accessToken.getToken()));
        }
        log.info("User Successfully Created");
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(savedUser.getId(), savedUser.getEmail(), ResponseMessage.USER_CREATED, accessToken.getToken()));
    }


    public ResponseEntity<AuthResponse> login(LoginRequest request, HttpServletResponse response) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if(optionalUser.isEmpty()){
            log.info("Could not find any user with email : {}",request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(request.getEmail(),ResponseMessage.USER_NOT_FOUND));
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // At this point, authentication is successful
        User user = (User) authentication.getPrincipal();
        boolean isUserRoleAllowed=user.isUserRoleAllowed(request.getRole());
        if(!isUserRoleAllowed){
            log.info("user with email :{} not allowed for role:{}",request.getEmail(),request.getRole());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(user.getEmail(), ResponseMessage.USER_ROLE_NOT_ALLOWED));
        }
        AccessToken accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());
        RefreshToken refreshToken = jwtService.generateRefreshToken(user.getEmail());
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(!isLocal) // Set to false in local dev if not using HTTPS
                .path("/auth")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        log.info("User Successfully logged in with user email: {}", user.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(user.getId(), user.getEmail(), ResponseMessage.USER_CREATED, accessToken.getToken()));
    }

    public ResponseEntity<String> logout(String refreshToken, HttpServletResponse response) {
        try {
            String tokenId = jwtService.extractTokenId(refreshToken);
            refreshTokenRepository.deleteById(tokenId);
        } catch (Exception ex) {
            log.warn("Logout: invalid refresh token: {}", ex.getMessage());
            throw ex;
        }
        // Clear refresh token cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(!isLocal) // Set to false in local dev if not using HTTPS
                .path("/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("Logged out successfully.");
    }


}
