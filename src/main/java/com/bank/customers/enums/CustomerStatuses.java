package com.bank.customers.enums;

/**
 * Enum representing the status of a customer in the banking system.
 */
public enum CustomerStatuses {
    /**
     * Indicates that the customer is active and can use banking services.
     */
    ACTIVE,

    /**
     * Indicates that the customer is inactive, typically due to prolonged inactivity.
     */
    INACTIVE,

    /**
     * Indicates that the customer's account is temporarily suspended,
     *  often due to legal or compliance issues.
     */
    SUSPENDED
}