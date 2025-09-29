package com.circlerate.circle_rate.user.service;

import com.circlerate.circle_rate.auth.model.AccessToken;
import com.circlerate.circle_rate.auth.model.LoginType;
import com.circlerate.circle_rate.auth.model.RefreshToken;
import com.circlerate.circle_rate.auth.payload.AuthResponse;
import com.circlerate.circle_rate.auth.payload.LoginRequest;
import com.circlerate.circle_rate.auth.payload.SignupRequest;
import com.circlerate.circle_rate.auth.repository.RefreshTokenRepository;
import com.circlerate.circle_rate.auth.service.JwtService;
import com.circlerate.circle_rate.common.constants.ResponseMessage;
import com.circlerate.circle_rate.user.model.User;
import com.circlerate.circle_rate.user.repository.UserRepository;
import com.circlerate.circle_rate.user.utils.UserServiceUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

import static com.circlerate.circle_rate.config.ApplicationConfig.isLocal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserServiceUtils userServiceUtils;

    



    public ResponseEntity<AuthResponse> signup(SignupRequest request, HttpServletResponse response) {
        request.setLoginType(LoginType.CUSTOM);
        User user =userServiceUtils.createUser(request);
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
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(user.getId(), user.getEmail(), ResponseMessage.APPROVAL_RAISED, accessToken.getToken()));
        }
        log.info("User Successfully Created");
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(user.getId(), user.getEmail(), ResponseMessage.USER_CREATED, accessToken.getToken()));
    }


    public ResponseEntity<AuthResponse> login(LoginRequest request, HttpServletResponse response) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if(optionalUser.isEmpty()){
            log.info("Could not find any user with email : {}",request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(request.getEmail(),ResponseMessage.USER_NOT_FOUND));
        }
        User userFromRepo = optionalUser.get();
        if(!userFromRepo.getLoginType().equals(LoginType.CUSTOM)){
            log.info("User logged in with different login type: {}", userFromRepo.getLoginType());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthResponse(request.getEmail(), ResponseMessage.USER_SIGNEDUP_WITH_DIFFERENT_LOGIN_TYPE + userFromRepo.getLoginType()));
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // At this point, authentication is successful
        User user = (User) authentication.getPrincipal();
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
        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(user.getId(), user.getEmail(), ResponseMessage.USER_SUCCESSFULLY_LOGGED_IN, accessToken.getToken()));
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

    public ResponseEntity<AuthResponse> refreshAccessToken(String refreshToken, HttpServletResponse response) {
        try {
            String email = jwtService.extractUsername(refreshToken);
            String tokenId = jwtService.extractTokenId(refreshToken);
            
            // Check if refresh token exists
            if (!refreshTokenRepository.existsById(tokenId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(email, ResponseMessage.INVALID_TOKEN));
            }
            
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(email, ResponseMessage.USER_NOT_FOUND));
            }
            
            // Check if refresh token is older than 5 days
            Optional<RefreshToken> existingToken = refreshTokenRepository.findById(tokenId);
            boolean shouldRotateToken = false;
            
            if (existingToken.isPresent()) {
                long daysSinceIssued = (System.currentTimeMillis() - existingToken.get().getIssuedAt().getTime()) / (1000 * 60 * 60 * 24);
                shouldRotateToken = daysSinceIssued >= 5;
            }
            
            AccessToken newAccessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());
            AuthResponse authResponse = new AuthResponse(user.getId(), user.getEmail(), ResponseMessage.ACCESS_TOKEN_REFRESHED, newAccessToken.getToken());
            
            // If token is older than 5 days, generate new refresh token and delete old one
            if (shouldRotateToken) {
                // Delete old refresh token
                refreshTokenRepository.deleteById(tokenId);
                // Generate new refresh token
                com.circlerate.circle_rate.auth.model.RefreshToken newRefreshToken = jwtService.generateRefreshToken(user.getEmail());
                
                // Set new refresh token as cookie
                ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
                        .httpOnly(true)
                        .secure(!isLocal) // Set to false in local dev if not using HTTPS
                        .path("/auth")
                        .maxAge(Duration.ofDays(7))
                        .sameSite("Strict")
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            }
            
            return ResponseEntity.ok(authResponse);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, ResponseMessage.INVALID_TOKEN));
        }
    }

    public ResponseEntity<String> revokeRefreshToken(String refreshToken) {
        try {
            String tokenId = jwtService.extractTokenId(refreshToken);
            if (refreshTokenRepository.existsById(tokenId)) {
                refreshTokenRepository.deleteById(tokenId);
                return ResponseEntity.ok("Refresh token revoked successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Refresh token not found");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }


}
