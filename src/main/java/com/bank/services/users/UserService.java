package com.bank.services.users;

import com.bank.dtos.PagedResponseDto;
import com.bank.dtos.users.UserDto;
import com.bank.enums.users.UserTypes;
import com.bank.exceptions.DomainException;
import com.bank.repos.users.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@SuppressWarnings("unused")
@Service
public class UserService  {
    private final UserRepository _userRepository;
    private final ModelMapper _modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        _userRepository = userRepository;
        _modelMapper = modelMapper;
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

    public UserDto EditUser(UserDto userDto) {
            return editUser(userDto);
    }

    public Boolean isAdmin(Long userId) {
        var user = _userRepository.findById(userId).orElse(null);
        if(user == null)
            throw new DomainException("error.auth.notFound");

        return user.getUserType() == UserTypes.ROLE_ADMIN;
    }
}
