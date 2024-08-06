package com.bank.dtos.loans;

import com.bank.enums.accounts.Currencies;
import com.bank.dtos.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class InstallmentDto extends BaseDto {
    private Integer installmentNo;
    @DateTimeFormat(pattern ="yyyy-MM-dd")
    private LocalDate dueDate;
    private BigDecimal interestAmount;
    private BigDecimal principalAmount;
    private BigDecimal paymentAmount;
    private BigDecimal loanBalanceAmount;
    private Currencies currency;
    @DateTimeFormat(pattern ="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime paidDate;
    private Boolean paid;

    public InstallmentDto(Integer installmentNo, LocalDate dueDate, BigDecimal interestAmount, BigDecimal principalAmount,
                          BigDecimal paymentAmount,BigDecimal loanBalanceAmount,  Currencies currency) {
        setInstallmentNo(installmentNo);
        setDueDate(dueDate);
        setInterestAmount(interestAmount);
        setPrincipalAmount(principalAmount);
        setPaymentAmount(paymentAmount);
        setLoanBalanceAmount(loanBalanceAmount);
        setCurrency(currency);
    }
}
