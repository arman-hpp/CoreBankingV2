package com.bank.transactions.models;

import com.bank.core.enums.Currencies;
import com.bank.transactions.enums.TransactionTypes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.bank.accounts.models.Account;
import com.bank.core.models.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Transactions")
@JsonIgnoreProperties({"account"})
@SuppressWarnings("JpaDataSourceORMInspection")
public class Transaction extends BaseEntity {
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TransactionTypes transactionType;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "trace_no", length = 36, nullable = false, unique = true)
    private String traceNo;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Currencies currency;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}