package com.bank.ledgers.models;

import com.bank.core.models.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Ledgers")
public class Ledger extends BaseEntity {
    @Column(name = "code", length = 20, nullable = false, unique = true)
    private String code;

    @Column(name = "title", length = 40, nullable = false)
    private String title;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "ledger", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<GeneralLedger> generalLedgers = new HashSet<>();
}
