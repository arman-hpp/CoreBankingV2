package com.bank.loans.services;

import com.bank.loans.dtos.PayInstallmentInputDto;
import com.bank.loans.dtos.SumNonPaidInstallmentOutputDto;
import com.bank.transactions.dtos.TransferDto;
import com.bank.core.exceptions.BusinessException;
import com.bank.loans.models.Installment;
import com.bank.loans.repos.InstallmentRepository;
import com.bank.loans.repos.LoanRepository;
import com.bank.transactions.services.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PayInstallmentService {
    private final InstallmentRepository _installmentRepository;
    private final TransactionService _transactionService;
    private final LoanRepository _loanRepository;

    public PayInstallmentService(InstallmentRepository installmentRepository,
                                 TransactionService transactionService,
                                 LoanRepository loanRepository) {
        _installmentRepository = installmentRepository;
        _transactionService = transactionService;
        _loanRepository = loanRepository;
    }

    public SumNonPaidInstallmentOutputDto sumNonPaidInstallment(Long loanId, Integer payInstallmentCount) {
        if (payInstallmentCount <= 0)
            throw new BusinessException("error.loan.installments.count.invalid");

        var installments = _installmentRepository
                .findTopCountByLoanIdAndPaidOrderByInstallmentNo(payInstallmentCount, loanId, false);

        if(payInstallmentCount > installments.size())
            throw new BusinessException("error.loan.installments.count.excess");

        var sumNonPaidInstallments = installments.stream()
                .map(Installment::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SumNonPaidInstallmentOutputDto(sumNonPaidInstallments);
    }

    @Transactional
    public void payInstallments(Long userId, PayInstallmentInputDto payInstallmentInputDto) {
        if(payInstallmentInputDto.getInstallmentCount() <= 0)
            throw new BusinessException("error.loan.installments.count.invalid");

        var loan = _loanRepository.findByLoanAccountId(payInstallmentInputDto.getLoanAccountId()).orElse(null);
        if(loan == null)
            throw new BusinessException("error.loan.noFound");

        var installments = _installmentRepository
                .findTopCountByLoanIdAndPaidOrderByInstallmentNo(
                        payInstallmentInputDto.getInstallmentCount(),
                        loan.getId(),
                        false);

        if(payInstallmentInputDto.getInstallmentCount() > installments.size())
            throw new BusinessException("error.loan.installments.count.excess");

        var sumInstallmentsAmount = installments.stream()
                .map(Installment::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (var installment : installments) {
            installment.setPaid(true);
            installment.setPaidDate(LocalDateTime.now());
        }

        _installmentRepository.saveAll(installments);

        var currency = installments.getFirst().getCurrency();

        var transferDto = new TransferDto(sumInstallmentsAmount,
                "Pay " + payInstallmentInputDto.getInstallmentCount() +
                        " installment(s) for loan Id " + loan.getId(),
                "Deposit for loan's installment(s) from account Id " + payInstallmentInputDto.getCustomerAccountId() + " and loan Id " + loan.getId(),
                payInstallmentInputDto.getCustomerAccountId(), payInstallmentInputDto.getLoanAccountId(), userId, currency);

        _transactionService.transfer(transferDto);
    }
}