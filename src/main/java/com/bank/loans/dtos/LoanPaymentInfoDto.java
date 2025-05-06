package com.bank.loans.dtos;

import com.bank.core.dtos.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanPaymentInfoDto extends BaseDto {
    private BigDecimal monthlyPaymentAmount;
    private BigDecimal overPaymentAmount;
    private List<InstallmentDto> installments;
}
