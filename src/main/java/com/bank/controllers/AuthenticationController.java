package com.bank.controllers;

import com.bank.dtos.users.*;
import com.bank.exceptions.DomainException;
import com.bank.services.users.AuthenticationService;
import com.bank.services.users.CaptchaService;
import com.bank.services.users.RefreshTokenCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService _authenticationService;
    private final RefreshTokenCookieService _refreshTokenCookieService;
    private final CaptchaService _captchaService;

    public AuthenticationController(AuthenticationService authenticationService,
                                    RefreshTokenCookieService refreshTokenCookieService,
                                    CaptchaService captchaService) {
        _authenticationService = authenticationService;
        _refreshTokenCookieService = refreshTokenCookieService;
        _captchaService = captchaService;
    }

    @GetMapping({"/", "/me"})
    public UserDto getCurrentUser() {
        return _authenticationService.loadCurrentUser();
    }

    @GetMapping("/captcha")
    public ResponseEntity<Map<String, String>> getCaptcha() {
        return ResponseEntity.ok(_captchaService.generateCaptcha());
    }

    @PostMapping("/login")
    public AccessTokenDto login(HttpServletResponse response, @RequestBody UserLoginInputDto input) {
        if(!_captchaService.verifyCaptcha(input.getCaptchaToken(), input.getCaptchaAnswer())){
            throw new DomainException("error.auth.captcha.invalid");
        }

        var userLoginDto = _authenticationService.authenticate(input);
        _refreshTokenCookieService.addRefreshToken(response, userLoginDto.getRefreshToken(), userLoginDto.getRefreshTokenExpiration());
        return new AccessTokenDto(userLoginDto.getUsername(), userLoginDto.getAccessToken());
    }

    @PostMapping("/register")
    public UserDto register(@RequestBody UserRegisterInputDto input) {
        return _authenticationService.register(input);
    }

    @PostMapping("/change_password")
    public void changeUserPassword(@RequestBody UserChangePasswordInputDto input) {
        var userId = _authenticationService.loadCurrentUserId().orElse(null);
        if (userId == null) {
            throw new DomainException("error.auth.credentials.invalid");
        }

        input.setId(userId);
        _authenticationService.changePassword(input);
    }

    @PostMapping("/refresh_token")
    public AccessTokenDto refreshToken(HttpServletRequest request) {
        var refreshToken = _refreshTokenCookieService.getRefreshToken(request);
        var input = new RefreshTokenInputDto(refreshToken);

        return _authenticationService.reAuthenticate(input);
    }

    @PostMapping("/logout")
    public void logout(@NonNull HttpServletResponse response) {
        var userId = _authenticationService.loadCurrentUserId().orElse(null);
        if (userId == null) {
            return;
        }

        _authenticationService.revokeAuthenticate(userId);
        _refreshTokenCookieService.deleteRefreshToken(response);
    }
}