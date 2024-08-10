package com.bank.services.users;

import com.bank.dtos.PagedResponseDto;
import com.bank.dtos.users.UserChangePasswordInputDto;
import com.bank.dtos.users.UserDto;
import com.bank.dtos.users.UserLoginInputDto;
import com.bank.dtos.users.UserRegisterInputDto;
import com.bank.enums.users.UserState;
import com.bank.enums.users.UserTypes;
import com.bank.exceptions.DomainException;
import com.bank.models.users.User;
import com.bank.repos.users.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@SuppressWarnings("unused")
@Service
public class UserService  {
    private final UserRepository _userRepository;
    private final ModelMapper _modelMapper;
    private final PasswordEncoder _passwordEncoder;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        _userRepository = userRepository;
        _modelMapper = modelMapper;
        _passwordEncoder = passwordEncoder;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = _userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("The user cannot be found");
        }

        return user;
    }

    public UserDto loadUser(Long userId) {
        var user = _userRepository.findById(userId).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        return _modelMapper.map(user, UserDto.class);
    }

    public PagedResponseDto<UserDto> loadUsers(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "Id"));
        var users = _userRepository.findAll(pageable);
        var results = users.map(user -> _modelMapper.map(user, UserDto.class));
        return new PagedResponseDto<>(results);
    }

    public void removeUser(Long userId) {
        var user = _userRepository.findById(userId).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        _userRepository.delete(user);
    }

    public UserDto editUser(UserDto userDto) {
        var user = _userRepository.findById(userDto.getId()).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        _modelMapper.map(userDto, user);
        _userRepository.save(user);

        return userDto;
    }

    public UserDto addOrEditUser(UserDto userDto) {
        if(userDto.getId()  == null || userDto.getId() <= 0) {
           return register(
                    new UserRegisterInputDto(userDto.getUsername(), userDto.getPassword(),
                            userDto.getPassword(), true));
        }
        else {
           return editUser(userDto);
        }
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

    public Boolean isAdmin(Long userId) {
        var user = _userRepository.findById(userId).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        return user.getUserType() == UserTypes.ROLE_ADMIN;
    }
}
