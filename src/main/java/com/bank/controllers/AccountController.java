package com.bank.controllers;

import com.bank.dtos.PagedResponseDto;
import com.bank.dtos.accounts.AccountDto;
import com.bank.services.accounts.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService _accountService;

    public AccountController(AccountService accountService) {
        _accountService = accountService;
    }

    @GetMapping("/")
    public PagedResponseDto<AccountDto> getAllAccounts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return _accountService.loadAccounts(page, size);
    }

    @GetMapping("/by/customer")
    public PagedResponseDto<AccountDto> getCustomerAccounts(
            @RequestParam Long customerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return _accountService.loadCustomerAccounts(customerId, page, size);
    }

    @GetMapping("/banks")
    public PagedResponseDto<AccountDto> getAllBankAccounts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return _accountService.loadBankAccounts(page, size);
    }

    @GetMapping("/{id}")
    public AccountDto getAccountById(@PathVariable Long id) {
        return _accountService.loadAccount(id);
    }

    @PostMapping("/")
    public AccountDto addAccount(@RequestBody AccountDto accountDto) {
        return _accountService.addAccount(accountDto);
    }
}
