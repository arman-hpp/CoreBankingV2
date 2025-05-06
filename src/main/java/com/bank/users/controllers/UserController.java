package com.bank.users.controllers;

import com.bank.core.dtos.PagedResponseDto;
import com.bank.users.dtos.EditUserInputDto;
import com.bank.users.dtos.UserDto;
import com.bank.users.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService _userService;

    public UserController(UserService userService) {
        _userService = userService;
    }

    @GetMapping("/")
    public PagedResponseDto<UserDto> getAllUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return _userService.loadUsers(page, size);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return _userService.loadUser(id);
    }

    @PostMapping("/")
    public UserDto editUser(@RequestBody EditUserInputDto userDto) {
        return _userService.editUser(userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        _userService.removeUser(id);
    }
}
