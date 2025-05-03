package com.bank.controllers;

import com.bank.dtos.loans.*;
import com.bank.enums.accounts.Currencies;
import com.bank.exceptions.BusinessException;
import com.bank.services.loans.*;
import com.bank.services.users.AuthenticationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/loan")
public class LoanController {
    private final LoanService _loanService;
    private final LoanConditionsService _loanConditionsService;
    private final InstallmentService _installmentService;
    private final AuthenticationService _authenticationService;
    private final DepositLoanService _depositLoanService;
    private final PayInstallmentService _payInstallmentService;

    public LoanController(LoanService loanService,
                          LoanConditionsService loanConditionsService,
                          InstallmentService installmentService,
                          AuthenticationService authenticationService,
                          DepositLoanService depositLoanService,
                          PayInstallmentService payInstallmentService) {
        _loanService = loanService;
        _loanConditionsService = loanConditionsService;
        _installmentService = installmentService;
        _authenticationService = authenticationService;
        _depositLoanService = depositLoanService;
        _payInstallmentService = payInstallmentService;
    }

    @GetMapping({"/"})
    public List<LoanDto> getLoans() {
        return _loanService.loadLoans();
    }

    @GetMapping({"/{id}"})
    public LoanDto getLoan(@PathVariable Long id) {
        return _loanService.loadLoan(id);
    }

    @GetMapping({"/by/account"})
    public List<LoanDto> getLoansByAccountId(@RequestParam Long accountId) {
        return _loanService.loadLoansByAccountId(accountId);
    }

    @GetMapping({"/by/customer"})
    public List<LoanDto> getLoansByCustomerId(@RequestParam Long accountId) {
        return _loanService.loadLoansByCustomerId(accountId);
    }

    @GetMapping({"/conditions"})
    public LoanConditionsDto getLoansByCustomerId(@RequestParam String currency) {
        Currencies currencyEnum = null;
        if (currency != null) {
            currencyEnum = Currencies.valueOf(currency);
        }

        return _loanConditionsService.loadLoanCondition(currencyEnum);
    }

    @PostMapping("/conditions")
    public void editLoanConditions(@ModelAttribute LoanConditionsDto loanConditionsDto) {
        _loanConditionsService.editLoanConditions(loanConditionsDto);
    }

    @GetMapping({"/installments"})
    public List<InstallmentDto> getInstallments(@RequestParam Long loanId) {
        return _installmentService.loadInstallments(loanId);
    }

    @GetMapping({"/installments/sumNonPaid"})
    public SumNonPaidInstallmentOutputDto sumNonPaidInstallment(@RequestParam Long loadId,
                                                                @RequestParam Integer installmentCount) {
        return _payInstallmentService.sumNonPaidInstallment(loadId, installmentCount);
    }

    @PostMapping({"/"})
    public void addOrEditLoan(@RequestBody LoanDto loanDto) {
        _loanService.addOrEditLoan(loanDto);
    }

    @PostMapping("/delete")
    public void deleteLoan(@RequestBody LoanDto loanDto) {
        _loanService.removeLoan(loanDto.getId());
    }

    @PostMapping({"/deposit"})
    public void depositLoan(@RequestBody DepositLoanInputDto depositLoanInputDto) {
        var currentUserId = _authenticationService.loadCurrentUserId().orElse(null);
        if (currentUserId == null) {
            throw new BusinessException("error.auth.credentials.invalid");
        }

        _depositLoanService.depositLoan(currentUserId, depositLoanInputDto);
    }

    @PostMapping({"/installments/pay"})
    public void payInstallments(@RequestBody PayInstallmentInputDto payInstallmentInputDto) {
        var currentUserId = _authenticationService.loadCurrentUserId().orElse(null);
        if (currentUserId == null) {
            throw new BusinessException("error.auth.credentials.invalid");
        }

        _payInstallmentService.payInstallments(currentUserId, payInstallmentInputDto);
    }

    @PostMapping({"/calcInterests"})
    public List<LoanInterestStatisticsDto> calcInterests(@RequestBody LoanInterestSearchDto loanInterestSearchDto) {
        var future = _loanService
                .loadLoanSumInterests(loanInterestSearchDto.getFromDate(), loanInterestSearchDto.getToDate());

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException ex) {
            throw new BusinessException("error.public.unexpected");
        }
    }
}