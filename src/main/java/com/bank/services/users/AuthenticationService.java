package com.bank.services.users;

import com.bank.dtos.users.*;
import com.bank.enums.users.UserState;
import com.bank.enums.users.UserTypes;
import com.bank.exceptions.DomainException;
import com.bank.models.users.User;
import com.bank.repos.users.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
@Service
public class AuthenticationService  {
    private final UserRepository _userRepository;
    private final ModelMapper _modelMapper;
    private final TokenService _tokenService;
    private final AuthenticationManager _authenticationManager;
    private final PasswordEncoder _passwordEncoder;

    public AuthenticationService(UserRepository userRepository,
                                 ModelMapper modelMapper,
                                 TokenService tokenService,
                                 AuthenticationManager authenticationManager,
                                 PasswordEncoder passwordEncoder) {
        _userRepository = userRepository;
        _modelMapper = modelMapper;
        _tokenService = tokenService;
        _authenticationManager = authenticationManager;
        _passwordEncoder = passwordEncoder;
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

            var accessToken = _tokenService.generateAccessToken(user);
            var refreshToken = _tokenService.generateRefreshToken(user);
            var refreshTokenExpiration = _tokenService.getRefreshTokenExpiration();

            return new UserLoginOutputDto(input.getUsername(), accessToken, refreshToken, refreshTokenExpiration);
        } catch (AuthenticationException ex) {
            logFailedLogin(input.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    public AccessTokenDto reAuthenticate(RefreshTokenInputDto input) {
        if(input.getRefreshToken() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (!_tokenService.isRefreshTokenTokenValid(input.getRefreshToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        var username = _tokenService.extractUsernameFromRefreshToken(input.getRefreshToken());
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        var user = _userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        var newAccessToken = _tokenService.generateAccessToken(user);

        return new AccessTokenDto(username, newAccessToken);
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

    public void login(UserLoginInputDto userLoginInputDto) {
        var user = _userRepository.findByUsername(userLoginInputDto.getUsername()).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        if (!Objects.equals(user.getPassword(), userLoginInputDto.getPassword()))
            throw new DomainException("error.auth.credentials.invalid");
    }

    public UserDto register(UserRegisterInputDto userRegisterInputDto) {
        if(_userRepository.findByUsername(userRegisterInputDto.getUsername()).orElse(null) != null)
            throw new DomainException("error.auth.username.duplicate");

        if(!userRegisterInputDto.getPassword().equals(userRegisterInputDto.getRepeatPassword()))
            throw new DomainException("error.auth.password.mismatch");

        var user = _modelMapper.map(userRegisterInputDto, User.class);
        user.setUserType(UserTypes.ROLE_USER);
        user.setPassword(_passwordEncoder.encode(userRegisterInputDto.getPassword()));
        user.setUserState(UserState.Enabled);
        user.setFailedAttempt(0);
        user.setLastPasswordChangedDate(LocalDateTime.now());

        _userRepository.save(user);

        return _modelMapper.map(user, UserDto.class);
    }

    public void changePassword(UserChangePasswordInputDto userChangePasswordInputDto) {
        if(!Objects.equals(userChangePasswordInputDto.getNewPassword(), userChangePasswordInputDto.getRepeatNewPassword()))
            throw new DomainException("error.auth.password.mismatch");

        if(Objects.equals(userChangePasswordInputDto.getOldPassword(), userChangePasswordInputDto.getNewPassword()))
            throw new DomainException("error.auth.password.samePassword");

        var user = _userRepository.findById(userChangePasswordInputDto.getId()).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        if (!_passwordEncoder.matches(userChangePasswordInputDto.getOldPassword(), user.getPassword()))
            throw new DomainException("error.auth.credentials.invalid");

        user.setPassword(_passwordEncoder.encode(userChangePasswordInputDto.getNewPassword()));
        user.setLastPasswordChangedDate(LocalDateTime.now());

        _userRepository.save(user);
    }
}
