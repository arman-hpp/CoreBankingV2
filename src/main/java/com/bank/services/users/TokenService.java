package com.bank.services.users;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class TokenService {
    @Value("${security.jwt.access-token.secret-key}")
    private String accessTokenSecretKey;

    @Value("${security.jwt.refresh-token.secret-key}")
    private String refreshTokenSecretKet;

    @Value("${security.jwt.access-token.expiration-time}")
    private Integer accessTokenExpiration;

    @Getter
    @Value("${security.jwt.refresh-token.expiration-time}")
    private Integer refreshTokenExpiration;

    private final JwtService _jwtService;

    public TokenService(JwtService jwtService){

        _jwtService = jwtService;
    }

    public String generateAccessToken(UserDetails userDetails) {
        return _jwtService.generateToken(userDetails, accessTokenExpiration, getAccessTokenSignInKey());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return _jwtService.generateToken(userDetails, refreshTokenExpiration, getRefreshTokenSignInKey());
    }

    public boolean isAccessTokenTokenValid(String token) {
        return _jwtService.isTokenValid(token, getAccessTokenSignInKey());
    }

    public boolean isRefreshTokenTokenValid(String token) {
        return _jwtService.isTokenValid(token, getRefreshTokenSignInKey());
    }

    public String extractUsernameFromAccessToken(String token) {
        return _jwtService.extractClaim(token, getAccessTokenSignInKey(), Claims::getSubject);
    }

    public String extractUsernameFromRefreshToken(String token) {
        return _jwtService.extractClaim(token, getRefreshTokenSignInKey(), Claims::getSubject);
    }

    private SecretKey getAccessTokenSignInKey() {
        var keyBytes = Decoders.BASE64.decode(accessTokenSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getRefreshTokenSignInKey() {
        var keyBytes = Decoders.BASE64.decode(refreshTokenSecretKet);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}