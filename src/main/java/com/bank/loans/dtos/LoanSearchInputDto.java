package com.bank.loans.dtos;

import com.bank.core.dtos.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanSearchInputDto extends BaseDto {
    private Long loanId;
}
