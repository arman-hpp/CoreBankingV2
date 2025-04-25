package com.bank.services.users;

import com.bank.utils.utils.JwtUtils;
import io.jsonwebtoken.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class TokenService {
    @Value("${security.jwt.access-token.secret-key}")
    private String accessTokenSecretKey;

    @Value("${security.jwt.refresh-token.secret-key}")
    private String refreshTokenSecretKey;

    @Value("${security.jwt.access-token.expiration-time}")
    private Integer accessTokenExpiration;

    @Getter
    @Value("${security.jwt.refresh-token.expiration-time}")
    private Integer refreshTokenExpiration;

    public String generateAccessToken(UserDetails userDetails) {
        return JwtUtils.generateToken(
                userDetails.getUsername(), accessTokenExpiration, accessTokenSecretKey, new HashMap<>());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return JwtUtils.generateToken(
                userDetails.getUsername(), refreshTokenExpiration, refreshTokenSecretKey, new HashMap<>());
    }

    public boolean isAccessTokenTokenValid(String token) {
        return JwtUtils.isTokenValid(token, accessTokenSecretKey);
    }

    public boolean isRefreshTokenTokenValid(String token) {
        return JwtUtils.isTokenValid(token, refreshTokenSecretKey);
    }

    public String extractUsernameFromAccessToken(String token) {
        return JwtUtils.extractClaim(token, accessTokenSecretKey, Claims::getSubject);
    }

    public String extractUsernameFromRefreshToken(String token) {
        return JwtUtils.extractClaim(token, refreshTokenSecretKey, Claims::getSubject);
    }
}