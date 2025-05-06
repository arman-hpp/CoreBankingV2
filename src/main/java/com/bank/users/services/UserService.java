package com.bank.users.services;

import com.bank.core.dtos.PagedResponseDto;
import com.bank.users.dtos.EditUserInputDto;
import com.bank.users.dtos.UserDto;
import com.bank.core.exceptions.BusinessException;
import com.bank.users.repos.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService  {
    private final UserRepository _userRepository;
    private final ModelMapper _modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        _userRepository = userRepository;
        _modelMapper = modelMapper;
    }

    @Cacheable(value = "users", key="'users-page-'+#page + '-' + #size")
    public PagedResponseDto<UserDto> loadUsers(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "Id"));
        var users = _userRepository.findAll(pageable);
        var results = users.map(user -> _modelMapper.map(user, UserDto.class));
        return new PagedResponseDto<>(results);
    }

    @Cacheable(value = "user", key = "#userId")
    public UserDto loadUser(Long userId) {
        var user = _userRepository.findById(userId).orElse(null);
        if(user == null)
            throw new BusinessException("error.auth.notFound");

        return _modelMapper.map(user, UserDto.class);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true)
    }, put = {
            @CachePut(cacheNames = "user", key = "#userDto.id")
    })
    public UserDto editUser(EditUserInputDto userDto) {
        var user = _userRepository.findById(userDto.getId()).orElse(null);
        if(user == null)
            throw new BusinessException("error.auth.notFound");

        _modelMapper.map(userDto, user);
        _userRepository.save(user);

        return _modelMapper.map(user, UserDto.class);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(cacheNames = "user", key = "#userId")
    })
    public void removeUser(Long userId) {
        var user = _userRepository.findById(userId).orElse(null);
        if(user == null)
            throw new BusinessException("error.auth.notFound");

        try {
            _userRepository.delete(user);
        }
        catch (DataIntegrityViolationException ex) {
            throw new BusinessException("error.public.dependent.entity");
        }
    }
}
