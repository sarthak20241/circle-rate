package com.circlerate.circle_rate.user.utils;

import com.circlerate.circle_rate.auth.model.Role;
import com.circlerate.circle_rate.auth.payload.SignupRequest;
import com.circlerate.circle_rate.common.constants.ResponseMessage;
import com.circlerate.circle_rate.common.exception.custom_exception.UserAlreadyExistsException;
import com.circlerate.circle_rate.user.model.ClientProfile;
import com.circlerate.circle_rate.user.model.TempProfile;
import com.circlerate.circle_rate.user.model.User;
import com.circlerate.circle_rate.user.repository.UserRepository;
import com.circlerate.circle_rate.user.service.ApprovalService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceUtils {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApprovalService approvalService;

    

    public User createUser(SignupRequest request){
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if(optionalUser.isPresent()){
            User existingUser = optionalUser.get();
            log.info("User already exists with email : {} for role: {} ", existingUser.getEmail(),existingUser.getRole());
            throw new UserAlreadyExistsException(ResponseMessage.USER_ALREADY_EXISTS + existingUser.getRole());
        }
        //TODO: valid password check
        if(!request.checkValidPassword()){

        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getEmail(),encodedPassword,request.getFirstName(),request.getLastName(),request.getLoginType());
        if(request.getRole()!= Role.CLIENT){
            log.info("Approval Raised for user: {} for role: {}", request.getEmail(), request.getRole());
            approvalService.requestApproval(user,request.getRole());
            user.setRole(Role.TEMP_ROLE);
            user.setUserProfile(new TempProfile());
        }
        else{
            user.setRole(request.getRole());
            user.setUserProfile(new ClientProfile());
        }
        return userRepository.save(user);
    }
}
