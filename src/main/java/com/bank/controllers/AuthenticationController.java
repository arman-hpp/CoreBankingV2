package com.bank.controllers;

import com.bank.dtos.users.*;
import com.bank.exceptions.DomainException;
import com.bank.services.users.AuthenticationService;
import com.bank.services.users.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService _authenticationService;
    private final UserService _userService;

    public AuthenticationController(AuthenticationService authenticationService,
                                    UserService userService) {
        _authenticationService = authenticationService;
        _userService = userService;
    }

    @GetMapping({"/","/me"})
    public UserDto getCurrentUser() {
        return _authenticationService.loadCurrentUser();
    }

    @PostMapping("/login")
    public UserLoginOutputDto login(HttpServletResponse response, @RequestBody UserLoginInputDto input) {
        var result = _authenticationService.authenticate(input);
        response.addCookie(new Cookie("ACCESS_TOKEN", result.accessToken));

        return result;
    }

    @PostMapping("/register")
    public UserDto register(@RequestBody UserRegisterInputDto input) {
        return _userService.register(input);
    }

    @PostMapping("/change_password")
    public void changeUserPassword(@RequestBody UserChangePasswordInputDto input) {
        var userId = _authenticationService.loadCurrentUserId().orElse(null);
        if(userId == null) {
            throw new DomainException("error.auth.credentials.invalid");
        }

        input.setId(userId);
        _userService.changePassword(input);
    }
}