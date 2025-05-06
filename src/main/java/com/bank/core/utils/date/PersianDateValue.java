package com.bank.core.utils.date;

/**
 * An immutable value object representing a date in the Persian (Jalali) calendar.
 * This record holds the year, month, and day components of a Persian date.
 * It is primarily used as a simple data transfer object between conversion utilities.
 *
 * @param year  the Persian year (e.g., 1403)
 * @param month the Persian month (1 through 12)
 * @param day   the Persian day of the month (1 through 31)
 */
public record PersianDateValue(int year, int month, int day) {
    // No additional logic is required here since 'record' automatically
    // provides constructors, accessors, equals, hashCode, and toString methods.
}