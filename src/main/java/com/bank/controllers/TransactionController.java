package com.bank.controllers;

import com.bank.dtos.transactions.TransactionDto;
import com.bank.exceptions.DomainException;
import com.bank.services.transactions.TransactionService;
import com.bank.services.users.AuthenticationService;
import com.bank.utils.utils.ConvertorUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService _transactionService;
    private final AuthenticationService _authenticationService;

    public TransactionController(TransactionService transactionService,
                                 AuthenticationService authenticationService) {
        _transactionService = transactionService;
        _authenticationService = authenticationService;
    }

    @GetMapping("/by/account")
    public List<TransactionDto> loadLastTransactions(@RequestParam String accountId) {
        var accountIdLong = ConvertorUtils.tryParseLong(accountId, null);
        return  _transactionService.loadLastAccountTransactions(accountIdLong);
    }

    @GetMapping("/by/branch")
    public List<TransactionDto> loadLastBranchTransactions() {
        return  _transactionService.loadLastBranchTransactions();
    }

    @PostMapping("/")
    public void doTransaction(@RequestBody TransactionDto transactionDto){
        var currentUserId = _authenticationService.loadCurrentUserId().orElse(null);
        if (currentUserId == null) {
            throw new DomainException("error.auth.credentials.invalid");
        }
        transactionDto.setUserId(currentUserId);
        _transactionService.doTransaction(transactionDto);
    }
}
