package com.bank.ledgers.enums;

/**
 * Enum representing the nature of a ledger in the banking system.
 */
public enum LedgerNatures {
    /**
     * Indicates that the ledger nature is not specified or unknown.
     */
    UNKNOWN,

    /**
     * Represents a ledger that can act as both debtor and creditor.
     */
    BOTH,

    /**
     * Represents a ledger that primarily records debts owed (debit balance).
     */
    DEBTOR,

    /**
     * Represents a ledger that primarily records amounts owed to others (credit balance).
     */
    CREDITOR
}