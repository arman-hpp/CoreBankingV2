package com.bank.accounts.dtos;

import com.bank.core.dtos.BaseDto;
import com.bank.core.enums.Currencies;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddAccountRequestDto extends BaseDto {
    private Long subLedgerId;
    private Long customerId;
    private Currencies currency;
}
