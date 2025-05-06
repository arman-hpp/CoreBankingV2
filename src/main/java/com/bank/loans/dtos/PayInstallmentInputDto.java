package com.bank.loans.dtos;

import com.bank.core.enums.Currencies;
import com.bank.core.dtos.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PayInstallmentInputDto extends BaseDto {
    private Long customerAccountId;
    private Long loanAccountId;
    private Integer InstallmentCount;
    private BigDecimal Amount;
    private String accountCustomerName;
    private BigDecimal accountBalance;
    private Currencies accountCurrency;
}