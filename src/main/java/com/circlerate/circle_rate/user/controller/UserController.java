package com.circlerate.circle_rate.user.controller;

import com.circlerate.circle_rate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    @SuppressWarnings("unused")
    private final UserService userService;

    
}
