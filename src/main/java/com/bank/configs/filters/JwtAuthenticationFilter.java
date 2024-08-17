package com.bank.configs.filters;

import com.bank.models.users.User;
import com.bank.services.users.TokenService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService _tokenService;
    private final UserDetailsService _userDetailsService;

    public JwtAuthenticationFilter(
            TokenService tokenService,
            UserDetailsService userDetailsService) {
        _tokenService = tokenService;
        _userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        var accessToken = getAccessTokenFromHeader(request);
        if(accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        var username = _tokenService.extractUsernameFromAccessToken(accessToken);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (username == null || authentication != null) {
            filterChain.doFilter(request, response);
            return;
        }

        var userDetails = _userDetailsService.loadUserByUsername(username);
        if(!(userDetails instanceof User user)) {
            filterChain.doFilter(request, response);
            return;
        }

        if(user.getAccessToken() == null || !user.getAccessToken().equals(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromHeader(@NonNull HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        var accessToken = authHeader.substring(7);
        if (!_tokenService.isAccessTokenTokenValid(accessToken)) {
            return null;
        }

        return accessToken;
    }
}