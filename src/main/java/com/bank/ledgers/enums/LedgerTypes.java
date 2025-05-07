package com.bank.ledgers.enums;

/**
 * Enum representing the type of ledger in the banking system.
 */
public enum LedgerTypes {
    /**
     * Indicates that the ledger type is not specified or unknown.
     */
    UNKNOWN,

    /**
     * Represents a permanent ledger used for long-term financial records.
     */
    PERMANENT,

    /**
     * Represents a temporary ledger used for short-term or provisional records.
     */
    TEMPORARY,

    /**
     * Represents a control ledger used for oversight and reconciliation purposes.
     */
    CONTROL
}