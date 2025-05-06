package com.bank.loans.services;

import com.bank.core.exceptions.BusinessException;
import com.bank.loans.dtos.LoanConditionsDto;
import com.bank.loans.dtos.LoanDto;
import com.bank.loans.services.interfaces.ILoanValidator;

public class DefaultLoanValidator implements ILoanValidator {
    public void validate(LoanConditionsDto loanConditionsDto, LoanDto loanDto) {
        if (loanDto.getRefundDuration() < loanConditionsDto.getMinRefundDuration())
            throw new BusinessException("error.loan.conditions.minRefundDuration.invalid");

        if (loanDto.getRefundDuration() > loanConditionsDto.getMaxRefundDuration())
            throw new BusinessException("error.loan.conditions.maxRefundDuration.invalid");

        if (loanDto.getAmount().compareTo(loanConditionsDto.getMinAmount()) < 0)
            throw new BusinessException("error.loan.conditions.minAmount.invalid");

        if (loanDto.getAmount().compareTo(loanConditionsDto.getMaxAmount()) > 0)
            throw new BusinessException("error.loan.conditions.maxAmount.invalid");
    }
}
