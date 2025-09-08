package com.circlerate.circle_rate.auth.config;

import com.circlerate.circle_rate.auth.model.AccessToken;
import com.circlerate.circle_rate.auth.model.LoginType;
import com.circlerate.circle_rate.auth.model.RefreshToken;
import com.circlerate.circle_rate.auth.model.Role;
import com.circlerate.circle_rate.auth.payload.AuthResponse;
import com.circlerate.circle_rate.auth.payload.SignupRequest;
import com.circlerate.circle_rate.auth.service.JwtService;
import com.circlerate.circle_rate.common.constants.ResponseMessage;
import com.circlerate.circle_rate.common.exception.body.ErrorMessage;
import com.circlerate.circle_rate.common.exception.custom_exception.UserAlreadyExistsException;
import com.circlerate.circle_rate.common.utils.CommonUtility;
import com.circlerate.circle_rate.user.model.User;
import com.circlerate.circle_rate.user.repository.UserRepository;
import com.circlerate.circle_rate.user.utils.UserServiceUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;

import static com.circlerate.circle_rate.config.ApplicationConfig.isLocal;

@Slf4j
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    JwtService jwtService;
    @Autowired
    UserServiceUtils userServiceUtils;
    @Autowired
    UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String email = oAuth2User.getAttribute("email");
        log.info("google authentication successful for email: {} ",email);

        User user;
        AuthResponse authResponse;
        int httpStatus = HttpServletResponse.SC_CREATED;
        if(!userRepository.existsByEmail(email)){
            //google signup
            String state = request.getParameter("state");
            String role;
            try{
                role = new String(Base64.getDecoder().decode(state));
            }
            catch (IllegalArgumentException ex){
                CommonUtility.writeResponse(response, new ErrorMessage(HttpStatus.FORBIDDEN.value(),"User Not Signed Up"),HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            SignupRequest signupRequest = new SignupRequest(email,"google-signup",firstName,lastName, Role.valueOf(role), LoginType.GOOGLE);
            try{
                user = userServiceUtils.createUser(signupRequest);
                if((user.getRole() != signupRequest.getRole())){
                    log.info("user created without role: {} permissions", signupRequest.getRole() );
                    authResponse = new AuthResponse(user.getEmail(), ResponseMessage.APPROVAL_RAISED);
                }
                else{
                    log.info("User Successfully Created");
                    authResponse =  new AuthResponse(user.getEmail(), ResponseMessage.USER_CREATED);
                }
            }
            catch (UserAlreadyExistsException ex){
                CommonUtility.writeResponse(response, new ErrorMessage(HttpStatus.CONFLICT.value(),ex.getMessage()),HttpServletResponse.SC_CONFLICT);
                return;
            }
        }
        else{
            //google login
            user = userRepository.findByEmail(email).get();
            if(!user.getLoginType().equals(LoginType.GOOGLE)){
                log.info("User logged in with different login type: {}", user.getLoginType());
                CommonUtility.writeResponse(response, new AuthResponse(email, ResponseMessage.USER_SIGNEDUP_WITH_DIFFERENT_LOGIN_TYPE + user.getLoginType()), HttpServletResponse.SC_CONFLICT);
                return;
            }
            log.info("User Successfully logged in with user email: {}", user.getEmail());
            authResponse = new AuthResponse(user.getEmail(), ResponseMessage.USER_SUCCESSFULLY_LOGGED_IN);
            httpStatus = HttpServletResponse.SC_OK;
        }


        AccessToken accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());
        authResponse.setAccessToken(accessToken.getToken());
        authResponse.setUserId(user.getId());

        RefreshToken refreshToken = jwtService.generateRefreshToken(user.getEmail());
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(!isLocal) // Set to false in local dev if not using HTTPS
                .path("/auth")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        CommonUtility.writeResponse(response, authResponse, httpStatus);
    }
}

