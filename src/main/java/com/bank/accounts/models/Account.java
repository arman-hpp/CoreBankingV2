package com.bank.accounts.models;

import com.bank.accounts.enums.AccountTypes;
import com.bank.core.enums.Currencies;
import com.bank.ledgers.models.SubLedger;
import com.bank.transactions.models.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bank.core.models.BaseEntity;
import com.bank.customers.models.Customer;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Accounts")
public class Account extends BaseEntity {
    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Currencies currency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sub_ledger_id")
    private SubLedger subLedger;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Transaction> transactions = new HashSet<>();

    public Account(Long Id) {
        setId(Id);
    }

    public AccountTypes getDerivedAccountType() {
        var glTitle = this.subLedger.getGeneralLedger().getTitle();
        if("Bank Accounts".equals(glTitle))
            return AccountTypes.BANK_ACCOUNT;
        if("Customer Deposits".equals(glTitle))
            return AccountTypes.CUSTOMER_ACCOUNT;
        return AccountTypes.UNKNOWN_ACCOUNT;
    }
}