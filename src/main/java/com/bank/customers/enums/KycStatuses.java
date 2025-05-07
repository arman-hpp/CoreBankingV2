package com.bank.customers.enums;

/**
 * Enum representing the Know Your Customer (KYC) verification status of a customer.
 */
public enum KycStatuses {
    /**
     * Indicates that the customer's KYC documents are under review.
     */
    PENDING,

    /**
     * Indicates that the customer's KYC documents have been successfully verified.
     */
    VERIFIED,

    /**
     * Indicates that the customer's KYC documents were rejected due to issues or non-compliance.
     */
    REJECTED
}
