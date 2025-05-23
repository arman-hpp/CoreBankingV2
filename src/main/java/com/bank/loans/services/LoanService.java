package com.bank.loans.services;

import com.bank.accounts.dtos.AddAccountRequestDto;
import com.bank.core.exceptions.BusinessException;
import com.bank.accounts.models.Account;
import com.bank.loans.repos.InstallmentRepository;
import com.bank.loans.repos.LoanRepository;
import com.bank.loans.dtos.LoanDto;
import com.bank.loans.dtos.LoanInterestStatisticsDto;
import com.bank.customers.models.Customer;
import com.bank.loans.models.Loan;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.bank.accounts.services.AccountService;
import com.bank.customers.services.CustomerService;
import com.bank.loans.services.interfaces.ILoanValidator;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LoanService {
    private final LoanRepository _loanRepository;
    private final InstallmentRepository _installmentRepository;
    private final ILoanValidator _loanValidator;
    private final LoanConditionsService _loanConditionsService;
    private final AccountService _accountService;
    private final CustomerService _customerService;
    private final ModelMapper _modelMapper;

    public LoanService(LoanRepository loanRepository,
                       InstallmentRepository installmentRepository,
                       ILoanValidator loanValidator,
                       LoanConditionsService loanConditionsService,
                       AccountService accountService,
                       CustomerService customerService,
                       ModelMapper modelMapper) {
        _loanRepository = loanRepository;
        _installmentRepository = installmentRepository;
        _loanValidator = loanValidator;
        _loanConditionsService = loanConditionsService;
        _accountService = accountService;
        _customerService = customerService;
        _modelMapper = modelMapper;
    }

    public List<LoanDto> loadLoans() {
        var loans = _loanRepository.findLoanWithDetails();
        var loanDtoList = new ArrayList<LoanDto>();
        for (var loan : loans) {
            var loanDto = _modelMapper.map(loan, LoanDto.class);
            var account = loan.getCustomerAccount();
            var customer = loan.getCustomer();

            loanDto.setAccountId(account.getId());
            loanDto.setAccountCurrency(account.getCurrency());
            loanDto.setAccountBalance(account.getBalance());
            loanDto.setCustomerId(customer.getId());
            loanDto.setAccountCustomerName(customer.getFullName());

            loanDtoList.add(loanDto);
        }

        return loanDtoList;
    }

    public List<LoanDto> loadLoansByCustomerId(Long customerId) {
        var customerDto = _customerService.getById(customerId);
        if(customerDto == null)
            throw new BusinessException("error.customer.notFound");

        var loans = _loanRepository.findByCustomerIdOrderByRequestDate(customerId);
        var loanDtoList = new ArrayList<LoanDto>();
        for (var loan : loans) {
            var loanDto = _modelMapper.map(loan, LoanDto.class);
            loanDto.setAccountId(loan.getCustomerAccount().getId());
            loanDtoList.add(loanDto);
        }

        return loanDtoList;
    }

    public List<LoanDto> loadLoansByAccountId(Long accountId) {
        var accountDto = _accountService.loadAccount(accountId);
        if(accountDto == null)
            throw new BusinessException("error.account.notFound");

        var loans = _loanRepository.findByCustomerAccountIdOrderByRequestDate(accountId);
        var loanDtoList = new ArrayList<LoanDto>();
        for (var loan : loans) {
            var loanDto = _modelMapper.map(loan, LoanDto.class);
            loanDto.setAccountId(accountId);
            loanDtoList.add(loanDto);
        }

        return loanDtoList;
    }

    public LoanDto loadLoan(Long loanId) {
        var loan = _loanRepository.findLoanByIdWithDetails(loanId).orElse(null);
        if (loan == null)
            throw new BusinessException("error.loan.noFound");

        var loanDto = _modelMapper.map(loan, LoanDto.class);
        var account = loan.getCustomerAccount();
        var customer = loan.getCustomer();
        loanDto.setAccountId(account.getId());
        loanDto.setAccountBalance(account.getBalance());
        loanDto.setAccountCurrency(account.getCurrency());
        loanDto.setCustomerId(customer.getId());
        loanDto.setAccountCustomerName(customer.getFullName());

        return loanDto;
    }


    public void addLoan(LoanDto loanDto) {
        var loanConditionsDto = _loanConditionsService.loadLoanCondition(loanDto.getCurrency());
        _loanValidator.validate(loanConditionsDto, loanDto);

        var requestDto = new AddAccountRequestDto();
        requestDto.setCurrency(loanDto.getCurrency());
        requestDto.setCustomerId(loanDto.getCustomerId());
        requestDto.setSubLedgerId(loanDto.getSubLedgerId());
        var newAccountDto = _accountService.addAccount(requestDto);

        var loan = _modelMapper.map(loanDto, Loan.class);
        loan.setCustomer(new Customer(loanDto.getCustomerId()));
        loan.setCustomerAccount(new Account(loanDto.getAccountId()));
        loan.setRequestDate(LocalDateTime.now());
        loan.setInterestRate(loanConditionsDto.getInterestRate());
        loan.setPaid(false);
        loan.setLoanAccount(new Account(newAccountDto.getId()));

        _loanRepository.save(loan);
    }

    public void editLoan(LoanDto loanDto) {
        var loanConditionsDto = _loanConditionsService.loadLoanCondition(loanDto.getCurrency());
        _loanValidator.validate(loanConditionsDto, loanDto);

        var loan = _loanRepository.findById(loanDto.getId()).orElse(null);
        if (loan == null)
            throw new BusinessException("error.loan.noFound");

        if (loan.getPaid())
            throw new BusinessException("error.loan.update.paidLoan");

        loan.setRefundDuration(loanDto.getRefundDuration());
        loan.setAmount(loanDto.getAmount());
        loan.setCustomer(new Customer(loanDto.getCustomerId()));
        loan.setCustomerAccount(new Account(loanDto.getAccountId()));

        _loanRepository.save(loan);
    }

    @Transactional
    public void addOrEditLoan(LoanDto loanDto) {
        if (loanDto.getId() == null || loanDto.getId() <= 0) {
            addLoan(loanDto);
        } else {
            editLoan(loanDto);
        }
    }

    public void removeLoan(Long loanId) {
        var loan = _loanRepository.findById(loanId).orElse(null);
        if (loan == null)
            throw new BusinessException("error.loan.noFound");

        if (loan.getPaid())
            throw new BusinessException("error.loan.remove.paidLoan");

        _loanRepository.delete(loan);
    }

    @Async
    public CompletableFuture<List<LoanInterestStatisticsDto>> loadLoanSumInterests(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        var loanInterestStatistics = _installmentRepository.sumTotalInterests(fromDateTime, toDateTime);
        var loanInterestStatisticsDtoList = new ArrayList<LoanInterestStatisticsDto>();
        for (var loanInterestStatistic : loanInterestStatistics) {
            loanInterestStatisticsDtoList.add(_modelMapper.map(loanInterestStatistic, LoanInterestStatisticsDto.class));
        }

        return CompletableFuture.completedFuture(loanInterestStatisticsDtoList);
    }
}