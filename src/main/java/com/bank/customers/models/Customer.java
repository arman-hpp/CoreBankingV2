package com.bank.customers.models;

import com.bank.core.models.GenericAttribute;
import com.bank.accounts.models.Account;
import com.bank.core.models.common.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bank.loans.models.Loan;
import com.bank.core.models.BaseEntity;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Customers")
@SuppressWarnings("JpaDataSourceORMInspection")
public class Customer extends BaseEntity {
    @Column(name = "first_name", length = 40, nullable = false)
    private String firstName;

    @Column(name = "last_name",length = 80, nullable = false)
    private String lastName;

    @Column(name = "address",length = 100, nullable = false)
    private String address;

    @ElementCollection(targetClass = Address.class, fetch = FetchType.EAGER)
    private Set<Address> addresses;

    @ElementCollection(targetClass = GenericAttribute.class, fetch = FetchType.EAGER)
    private Set<GenericAttribute> attributes;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Loan> loans = new HashSet<>();

    public Customer(Long Id) {
        setId(Id);
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
