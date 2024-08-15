package com.bank.services.users;

import com.bank.dtos.users.RefreshTokenInputDto;
import com.bank.dtos.users.UserDto;
import com.bank.dtos.users.UserLoginInputDto;
import com.bank.dtos.users.UserLoginOutputDto;
import com.bank.enums.users.UserTypes;
import com.bank.models.users.User;
import com.bank.repos.users.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@SuppressWarnings("unused")
@Service
public class AuthenticationService  {
    private final UserRepository _userRepository;
    private final ModelMapper _modelMapper;
    private final JwtService _jwtService;
    private final AuthenticationManager _authenticationManager;

    public AuthenticationService(UserRepository userRepository,
                                 ModelMapper modelMapper,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager) {
        _userRepository = userRepository;
        _modelMapper = modelMapper;
        _jwtService = jwtService;
        _authenticationManager = authenticationManager;
    }

    public UserLoginOutputDto authenticate(UserLoginInputDto input) {
        try {
            var authentication = _authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword())
            );

            var principal = authentication.getPrincipal();
            if (principal == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            if (!(principal instanceof User user)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            logSuccessfulLogin(user.getUsername());

            var accessToken = _jwtService.generateAccessToken(user);
            var refreshToken = _jwtService.generateRefreshToken(user);

            return new UserLoginOutputDto(input.getUsername(), accessToken, refreshToken);
        } catch (AuthenticationException ex) {
            logFailedLogin(input.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    public UserLoginOutputDto reAuthenticate(RefreshTokenInputDto input){
        if (!_jwtService.isRefreshTokenTokenValid(input.getRefreshToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        var username = _jwtService.extractUsernameFromRefreshToken(input.getRefreshToken());
        if(username == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        var user = _userRepository.findByUsername(username).orElse(null);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        var newAccessToken = _jwtService.generateAccessToken(user);
        var newRefreshToken = _jwtService.generateRefreshToken(user);

        return new UserLoginOutputDto(username, newAccessToken, newRefreshToken);
    }

    private void logFailedLogin(String username) {
        try {
            var user = _userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                return;
            }

            if (user.isEnabled() && user.isAccountNonLocked()) {
                if (user.isFailedAttemptsExceeded()) {
                    user.lock();
                } else {
                    user.increaseFailedAttempts();
                }
            } else if (!user.isAccountNonLocked()) {
                if (user.isLockTimeFinished()) {
                    user.unlock();
                }
            }

            _userRepository.save(user);
        } catch (Exception ex) {
            // ignore
        }
    }

    private void logSuccessfulLogin(String username) {
        try {
            var user = _userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                return;
            }

            user.setFailedAttempt(0);
            user.setLastLoginDate(LocalDateTime.now());

            _userRepository.save(user);
        } catch (Exception ex) {
            // ignore
        }
    }

    public Boolean isUserAuthenticated() {
        var context = SecurityContextHolder.getContext();
        if(context == null)
            return false;

        var authentication = context.getAuthentication();
        if (authentication == null) {
            return false;
        }

        if (authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        return authentication.isAuthenticated();
    }

    public Boolean isUserAdmin() {
        var user = loadCurrentUser();
        if(user == null){
            return false;
        } else {
            return user.getUserType() == UserTypes.ROLE_ADMIN;
        }
    }

    public UserDto loadCurrentUser() {
        var context = SecurityContextHolder.getContext();
        if(context == null)
            return null;

        var authentication = context.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        var principal = authentication.getPrincipal();
        if (principal == null) {
            return null;
        }

        if(principal instanceof User user){
            return _modelMapper.map(user, UserDto.class);
        }

        return new UserDto();
    }

    public Optional<String> loadCurrentUsername() {
        var user = loadCurrentUser();
        if(user == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(user.getUsername());
    }

    public Optional<Long> loadCurrentUserId() {
        var user = loadCurrentUser();
        if(user == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(user.getId());
    }
}
