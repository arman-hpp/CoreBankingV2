package com.bank.models.ledgers;

import com.bank.enums.ledgers.LedgerNatures;
import com.bank.enums.ledgers.LedgerTypes;
import com.bank.models.BaseEntity;
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
@Table(name = "Group_Ledgers")
@SuppressWarnings("JpaDataSourceORMInspection")
public class GroupLedger extends BaseEntity {
    @Column(name = "code", length = 20, nullable = false, unique = true)
    private String code;

    @Column(name = "title", length = 40, nullable = false)
    private String title;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "ledger_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private LedgerTypes ledgerType;

    @Column(name = "ledger_nature", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private LedgerNatures ledgerNature;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ledger_id")
    private Ledger ledger;

    @OneToMany(mappedBy = "groupLedger", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<GeneralLedger> generalLedgers = new HashSet<>();
}
