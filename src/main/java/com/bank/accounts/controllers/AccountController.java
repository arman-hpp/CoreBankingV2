package com.bank.accounts.controllers;

import com.bank.accounts.dtos.AccountResponseDto;
import com.bank.accounts.dtos.AddAccountRequestDto;
import com.bank.core.dtos.PagedResponseDto;
import com.bank.accounts.services.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService _accountService;

    public AccountController(AccountService accountService) {
        _accountService = accountService;
    }

    @GetMapping("/")
    public PagedResponseDto<AccountResponseDto> getAllAccounts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return _accountService.loadAccounts(page, size);
    }

    @GetMapping("/by/customer")
    public PagedResponseDto<AccountResponseDto> getCustomerAccounts(
            @RequestParam Long customerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return _accountService.loadCustomerAccounts(customerId, page, size);
    }

    @GetMapping("/by/subLedger")
    public PagedResponseDto<AccountResponseDto> getAccountsBySubLedger(
            @RequestParam Long subLedgerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return _accountService.loadAccountsBySubLedgerId(subLedgerId, page, size);
    }

    @GetMapping("/by/generalLedger")
    public PagedResponseDto<AccountResponseDto> getAccountsByGeneralLedger(
            @RequestParam Long generalLedgerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return _accountService.loadAccountsByGeneralLedgerId(generalLedgerId, page, size);
    }

    @GetMapping("/by/ledger")
    public PagedResponseDto<AccountResponseDto> getAccountsByLedger(
            @RequestParam Long ledgerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return _accountService.loadAccountsByLedgerId(ledgerId, page, size);
    }

    @GetMapping("/{id}")
    public AccountResponseDto getAccountById(@PathVariable Long id) {
        return _accountService.loadAccount(id);
    }

    @PostMapping("/")
    public AccountResponseDto addAccount(@RequestBody AddAccountRequestDto requestDto) {
        return _accountService.addAccount(requestDto);
    }
}
