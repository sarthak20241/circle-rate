package com.circlerate.circle_rate.user.service;

import com.circlerate.circle_rate.user.model.User;
import com.circlerate.circle_rate.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
}
