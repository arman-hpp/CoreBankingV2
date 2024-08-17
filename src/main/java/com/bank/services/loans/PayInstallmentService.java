package com.bank.services.loans;

import com.bank.dtos.loans.PayInstallmentInputDto;
import com.bank.dtos.loans.SumNonPaidInstallmentOutputDto;
import com.bank.dtos.transactions.TransferDto;
import com.bank.exceptions.DomainException;
import com.bank.models.loans.Installment;
import com.bank.repos.loans.InstallmentRepository;
import com.bank.services.transactions.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.services.accounts.AccountService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PayInstallmentService {
    private final InstallmentRepository _installmentRepository;
    private final TransactionService _transactionService;
    private final AccountService _accountService;

    public PayInstallmentService(InstallmentRepository installmentRepository,
                                 TransactionService transactionService,
                                 AccountService accountService) {
        _installmentRepository = installmentRepository;
        _transactionService = transactionService;
        _accountService = accountService;
    }

    public SumNonPaidInstallmentOutputDto sumNonPaidInstallment(Long loanId, Integer payInstallmentCount) {
        if (payInstallmentCount <= 0)
            throw new DomainException("error.loan.installments.count.invalid");

        var installments = _installmentRepository
                .findTopCountByLoanIdAndPaidOrderByInstallmentNo(payInstallmentCount, loanId, false);

        if(payInstallmentCount > installments.size())
            throw new DomainException("error.loan.installments.count.excess");

        var sumNonPaidInstallments = installments.stream()
                .map(Installment::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SumNonPaidInstallmentOutputDto(sumNonPaidInstallments);
    }

    @Transactional
    public void payInstallments(Long userId, PayInstallmentInputDto payInstallmentInputDto) {
        if(payInstallmentInputDto.getInstallmentCount() <= 0)
            throw new DomainException("error.loan.installments.count.invalid");

        var installments = _installmentRepository
                .findTopCountByLoanIdAndPaidOrderByInstallmentNo(
                        payInstallmentInputDto.getInstallmentCount(),
                        payInstallmentInputDto.getLoanId(),
                        false);

        if(payInstallmentInputDto.getInstallmentCount() > installments.size())
            throw new DomainException("error.loan.installments.count.excess");

        var sumInstallmentsAmount = installments.stream()
                .map(Installment::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (var installment : installments) {
            installment.setPaid(true);
            installment.setPaidDate(LocalDateTime.now());
        }

        _installmentRepository.saveAll(installments);

        var currency = installments.get(0).getCurrency();
        var bankAccountId = _accountService.loadBankAccount(currency).getId();

        var transferDto = new TransferDto(sumInstallmentsAmount,
                "Pay " + payInstallmentInputDto.getInstallmentCount() +
                        " installment(s) for loan Id " + payInstallmentInputDto.getLoanId(),
                "Deposit for loan's installment(s) from account Id " + payInstallmentInputDto.getAccountId() + " and loan Id " + payInstallmentInputDto.getLoanId(),
                payInstallmentInputDto.getAccountId(), bankAccountId, userId, currency);

        _transactionService.transfer(transferDto);
    }
}