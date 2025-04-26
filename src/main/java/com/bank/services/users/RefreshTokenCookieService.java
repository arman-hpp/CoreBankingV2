package com.bank.services.users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenCookieService {
    public String getRefreshToken(@NonNull HttpServletRequest request) {
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

    public void addRefreshToken(@NonNull HttpServletResponse response, String refreshToken, Integer expireTime) {
        var cookie = ResponseCookie.from("RefreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(expireTime / 1000)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void deleteRefreshToken(@NonNull HttpServletResponse response){
        var cookie = ResponseCookie.from("RefreshToken", "")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
