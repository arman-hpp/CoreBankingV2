package com.bank.controllers;

import com.bank.dtos.users.UserLoginInputDto;
import com.bank.dtos.users.UserLoginOutputDto;
import com.bank.services.users.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService _authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        _authenticationService = authenticationService;
    }

    @PostMapping("/")
    public UserLoginOutputDto authenticate(@RequestBody UserLoginInputDto input) {
        return _authenticationService.authenticate(input);
    }
}