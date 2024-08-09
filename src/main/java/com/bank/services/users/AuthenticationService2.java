package com.bank.services.users;

import com.bank.dtos.users.UserLoginInputDto;
import com.bank.dtos.users.UserLoginOutputDto;
import com.bank.repos.users.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService2 {
    private final UserRepository _userRepository;
    private final JwtService _jwtService;
    private final AuthenticationManager _authenticationManager;

    public AuthenticationService2(UserRepository userRepository,
                                  JwtService jwtService,
                                  AuthenticationManager authenticationManager) {
        _userRepository = userRepository;
        _jwtService = jwtService;
        _authenticationManager = authenticationManager;
    }

    public UserLoginOutputDto authenticate(UserLoginInputDto input) {
        _authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword())
        );

        var user = _userRepository.findByUsername(input.getUsername()).orElse(null);
        var jwtToken = _jwtService.generateToken(user);

        assert user != null;
        return new UserLoginOutputDto(input.getUsername(), jwtToken);
    }
}
