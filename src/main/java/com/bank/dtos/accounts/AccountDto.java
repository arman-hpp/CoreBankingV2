package com.bank.dtos.accounts;

import com.bank.enums.accounts.AccountTypes;
import com.bank.enums.accounts.Currencies;
import com.bank.dtos.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountDto extends BaseDto {
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