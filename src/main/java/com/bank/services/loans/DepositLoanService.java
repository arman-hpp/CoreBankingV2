package com.bank.services.loans;

import com.bank.dtos.loans.DepositLoanInputDto;
import com.bank.dtos.loans.LoanDto;
import com.bank.dtos.transactions.TransferDto;
import com.bank.exceptions.BusinessException;
import com.bank.models.loans.Installment;
import com.bank.repos.loans.InstallmentRepository;
import com.bank.repos.loans.LoanRepository;
import com.bank.services.loans.interfaces.ILoanCalculator;
import com.bank.services.loans.interfaces.ILoanValidator;
import com.bank.services.transactions.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class DepositLoanService {
    private final LoanRepository _loanRepository;
    private final InstallmentRepository _installmentRepository;
    private final ILoanValidator _loanValidator;
    private final LoanConditionsService _loanConditionsService;
    private final TransactionService _transactionService;
    private final ILoanCalculator _loanCalculator;
    private final ModelMapper _modelMapper;

    public DepositLoanService(LoanRepository loanRepository,
                              InstallmentRepository installmentRepository,
                              ILoanValidator loanValidator,
                              LoanConditionsService loanConditionsService,
                              TransactionService transactionService,
                              ILoanCalculator loanCalculator,
                              ModelMapper modelMapper) {
        _loanRepository = loanRepository;
        _installmentRepository = installmentRepository;
        _loanValidator = loanValidator;
        _loanConditionsService = loanConditionsService;
        _transactionService = transactionService;
        _loanCalculator = loanCalculator;
        _modelMapper = modelMapper;
    }

    @Transactional
    public void depositLoan(Long userId, DepositLoanInputDto depositLoanInputDto) {
        // TODO: get loan by customerAccount And loanAccount
        var loan = _loanRepository.findById(depositLoanInputDto.getLoanId()).orElse(null);
        if (loan == null)
            throw new BusinessException("error.loan.noFound");

        if (loan.getPaid())
            throw new BusinessException("error.loan.deposit.duplicate");

        loan.setPaid(true);
        loan.setFirstPaymentDate(LocalDate.now().plusMonths(1));
        loan.setDepositDate(LocalDateTime.now());

        var loanDto = _modelMapper.map(loan, LoanDto.class);

        var loanConditionsDto = _loanConditionsService.loadLoanCondition(loanDto.getCurrency());
        _loanValidator.validate(loanConditionsDto, loanDto);

        var loanPaymentInfo = _loanCalculator.calculate(loanDto);
        var list = new ArrayList<Installment>();
        for (var installmentsDto : loanPaymentInfo.getInstallments()) {
            var installment = _modelMapper.map(installmentsDto, Installment.class);
            installment.setLoan(loan);
            installment.setPaid(false);
            installment.setPaidDate(null);

            list.add(installment);
        }

        _installmentRepository.saveAll(list);
        _loanRepository.save(loan);

        var transferDto = new TransferDto(loan.getAmount(),
                "Debit for loan to customer account " + loan.getCustomerAccount().getId(),
                "Deposit loan Id " + depositLoanInputDto.getLoanId(),
                loan.getLoanAccount().getId(), loan.getCustomerAccount().getId(), userId, loan.getCurrency());

        _transactionService.transfer(transferDto);
    }
}