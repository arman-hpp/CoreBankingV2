package com.bank.accounts.dtos;

import com.bank.accounts.enums.AccountTypes;
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
public class AccountResponseDto extends BaseDto {
    private Long id;
    private BigDecimal balance;
    private Currencies currency;
    private Long subLedgerId;
    private Long generalLedgerId;
    private Long ledgerId;
    private Long customerId;
    private String customerName;
    private AccountTypes accountType;
}