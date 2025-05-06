package com.bank.loans.dtos;

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
public class SumNonPaidInstallmentOutputDto extends BaseDto {
    private BigDecimal sumNonPaidInstallment;
}
