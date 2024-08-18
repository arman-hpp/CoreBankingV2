package com.bank.controllers;

import com.bank.dtos.PagedResponseDto;
import com.bank.dtos.users.UserDto;
import com.bank.services.users.UserService;
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
    public UserDto editUser(@RequestBody UserDto userDto) {
        return _userService.editUser(userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        _userService.removeUser(id);
    }
}
