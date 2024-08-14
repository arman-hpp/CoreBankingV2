package com.bank.services.users;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.access-token.secret-key}")
    private String accessTokenSecretKey;

    @Value("${security.jwt.refresh-token.secret-key}")
    private String refreshTokenSecretKet;

    @Value("${security.jwt.access-token.expiration-time}")
    private long accessTokenExpiration;

    @Value("${security.jwt.refresh-token.expiration-time}")
    private long refreshTokenExpiration;

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, accessTokenExpiration, getAccessTokenSignInKey());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, refreshTokenExpiration, getRefreshTokenSignInKey());
    }

    public String generateToken(UserDetails userDetails, Long expireTime, SecretKey secretKey){
        return Jwts
                .builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey)
                .compact();
    }

    public boolean isAccessTokenTokenValid(String token) {
        return isTokenValid(token, getAccessTokenSignInKey());
    }

    public boolean isRefreshTokenTokenValid(String token) {
        return isTokenValid(token, getRefreshTokenSignInKey());
    }

    public boolean isTokenValid(String token, SecretKey secretKey){
        try {
            return !isTokenExpired(token, secretKey);
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public String extractUsernameFromAccessToken(String token) {
        return extractClaim(token, getAccessTokenSignInKey(), Claims::getSubject);
    }

    public String extractUsernameFromRefreshToken(String token) {
        return extractClaim(token, getRefreshTokenSignInKey(), Claims::getSubject);
    }

    public <T> T extractClaim(String token, SecretKey secretKey, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token, SecretKey secretKey) {
        return extractExpiration(token, secretKey).before(new Date());
    }

    private Date extractExpiration(String token, SecretKey secretKey) {
        return extractClaim(token, secretKey, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token, SecretKey secretKey) {
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

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