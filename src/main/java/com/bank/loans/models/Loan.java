package com.bank.loans.models;

import com.bank.core.enums.Currencies;
import com.bank.accounts.models.Account;
import com.bank.customers.models.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.bank.core.models.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Loans")
public class Loan extends BaseEntity {
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "interest_rate", nullable = false, precision = 7, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "refund_duration", nullable = false)
    private Integer refundDuration;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "deposit_date")
    private LocalDateTime depositDate;

    @Column(name = "first_payment_date")
    private LocalDate firstPaymentDate;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Currencies currency;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_account_id", nullable = false)
    private Account customerAccount;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "loan_account_id", nullable = false)
    private Account loanAccount;

    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Installment> installments = new HashSet<>();
}
