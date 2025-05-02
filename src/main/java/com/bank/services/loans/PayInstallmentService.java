package com.bank.services.loans;

import com.bank.dtos.loans.PayInstallmentInputDto;
import com.bank.dtos.loans.SumNonPaidInstallmentOutputDto;
import com.bank.dtos.transactions.TransferDto;
import com.bank.exceptions.BusinessException;
import com.bank.models.loans.Installment;
import com.bank.repos.loans.InstallmentRepository;
import com.bank.services.transactions.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PayInstallmentService {
    private final InstallmentRepository _installmentRepository;
    private final TransactionService _transactionService;

    public PayInstallmentService(InstallmentRepository installmentRepository,
                                 TransactionService transactionService) {
        _installmentRepository = installmentRepository;
        _transactionService = transactionService;
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

        var installments = _installmentRepository
                .findTopCountByLoanIdAndPaidOrderByInstallmentNo(
                        payInstallmentInputDto.getInstallmentCount(),
                        payInstallmentInputDto.getLoanId(),
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

        // TODO: it's better to load Loan to get loanAccountId
        var loanAccountId = payInstallmentInputDto.getLoanAccountId();

        var transferDto = new TransferDto(sumInstallmentsAmount,
                "Pay " + payInstallmentInputDto.getInstallmentCount() +
                        " installment(s) for loan Id " + payInstallmentInputDto.getLoanId(),
                "Deposit for loan's installment(s) from account Id " + payInstallmentInputDto.getAccountId() + " and loan Id " + payInstallmentInputDto.getLoanId(),
                payInstallmentInputDto.getAccountId(), loanAccountId, userId, currency);

        _transactionService.transfer(transferDto);
    }
}