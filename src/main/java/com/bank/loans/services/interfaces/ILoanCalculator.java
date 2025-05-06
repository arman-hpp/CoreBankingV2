package com.bank.loans.services.interfaces;

import com.bank.loans.dtos.LoanDto;
import com.bank.loans.dtos.LoanPaymentInfoDto;

public interface ILoanCalculator {
    LoanPaymentInfoDto calculate(LoanDto loan);
}
