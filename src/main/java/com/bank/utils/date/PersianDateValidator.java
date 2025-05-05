package com.bank.utils.date;

/**
 * Utility class for validating Persian (Jalali) date components.
 */
public final class PersianDateValidator {
    /**
     * Validates a given Persian date.
     *
     * @param year  the Persian year (must be between 1 and 9999)
     * @param month the Persian month (must be between 1 and 12)
     * @param day   the Persian day (must be within the valid range for the given month and year)
     * @throws IllegalArgumentException if any of the inputs are invalid
     */
    public static void validate(int year, int month, int day) {
        if (year < 1 || year > 9999) {
            throw new IllegalArgumentException("Year must be between 1 and 9999");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        var maxDay = PersianDateUtils.getMaxDayOfMonth(year, month);

        if (day < 1 || day > maxDay) {
            throw new IllegalArgumentException(
                    String.format("Day must be between 1 and %d for month %d", maxDay, month)
            );
        }
    }
}