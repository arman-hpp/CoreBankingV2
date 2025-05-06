package com.bank.loans.dtos;

import com.bank.core.enums.Currencies;
import com.bank.core.dtos.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanConditionsDto extends BaseDto {
    private Long id;
    private BigDecimal interestRate;
    private Integer minRefundDuration;
    private Integer maxRefundDuration;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    @DateTimeFormat(pattern ="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
    private Currencies currency;
}
