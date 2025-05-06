package com.bank.loans.services.interfaces;

import com.bank.loans.dtos.LoanConditionsDto;
import com.bank.loans.dtos.LoanDto;

public interface ILoanValidator {
    void validate(LoanConditionsDto loanConditionsDto, LoanDto loanDto);
}
