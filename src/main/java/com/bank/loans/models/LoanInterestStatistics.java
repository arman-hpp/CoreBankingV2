package com.bank.loans.models;

import com.bank.core.enums.Currencies;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class LoanInterestStatistics {
    private Currencies currency;
    private BigDecimal sumInterest;
}
