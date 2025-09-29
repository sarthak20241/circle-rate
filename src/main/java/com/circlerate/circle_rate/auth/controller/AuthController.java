package com.circlerate.circle_rate.auth.controller;


import com.circlerate.circle_rate.auth.payload.AuthResponse;
import com.circlerate.circle_rate.auth.payload.LoginRequest;
import com.circlerate.circle_rate.auth.payload.SignupRequest;
import com.circlerate.circle_rate.common.constants.ResponseMessage;
import com.circlerate.circle_rate.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signupUser(@RequestBody @Valid SignupRequest user, HttpServletResponse response){
        return userService.signup(user,response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody @Valid LoginRequest request, HttpServletResponse response){
        return userService.login(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutRequest(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response){
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessage.MISSING_TOKEN_EXCEPTION);
        }
        return userService.logout(refreshToken, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshAccessToken(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response){
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, ResponseMessage.MISSING_TOKEN_EXCEPTION));
        }
        return userService.refreshAccessToken(refreshToken, response);
    }

    @PostMapping("/revoke")
    public ResponseEntity<String> revokeRefreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken){
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessage.MISSING_TOKEN_EXCEPTION);
        }
        return userService.revokeRefreshToken(refreshToken);
    }

}
