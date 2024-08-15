package com.bank.controllers;

import com.bank.dtos.users.*;
import com.bank.exceptions.DomainException;
import com.bank.services.users.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService _authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        _authenticationService = authenticationService;
    }

    @GetMapping({"/","/me"})
    public UserDto getCurrentUser() {
        return _authenticationService.loadCurrentUser();
    }

    @PostMapping("/login")
    public AccessTokenDto login(HttpServletResponse response, @RequestBody UserLoginInputDto input) {
       var userLoginDto =  _authenticationService.authenticate(input);
        var cookie = ResponseCookie.from("RefreshToken", userLoginDto.refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(userLoginDto.getRefreshTokenExpiration() / 1000)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return new AccessTokenDto(userLoginDto.getUsername(), userLoginDto.accessToken);
    }

    @PostMapping("/register")
    public UserDto register(@RequestBody UserRegisterInputDto input) {
        return _authenticationService.register(input);
    }

    @PostMapping("/change_password")
    public void changeUserPassword(@RequestBody UserChangePasswordInputDto input) {
        var userId = _authenticationService.loadCurrentUserId().orElse(null);
        if(userId == null) {
            throw new DomainException("error.auth.credentials.invalid");
        }

        input.setId(userId);
        _authenticationService.changePassword(input);
    }

    @PostMapping("/refresh_token2")
    public AccessTokenDto refreshToken(HttpServletRequest request) {
        var refreshToken = getRefreshTokenFromCookie(request);
        var input = new RefreshTokenInputDto(refreshToken);
        return _authenticationService.reAuthenticate(input);
    }

    @SuppressWarnings("unused")
    private String getRefreshTokenFromCookie(@NonNull HttpServletRequest request){
        var cookies = request.getCookies();
        if (cookies != null) {
            for (var cookie : cookies) {
                if (cookie.getName().equals("RefreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}