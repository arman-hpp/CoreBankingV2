package com.bank.utils.date;

public final class PersianDateValidator {
    public static void validate(int year, int month, int day) {
        if (year < 1 || year > 9999) {
            throw new IllegalArgumentException("Year must be positive");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        int maxDay = (month < 7) ? 31 : 30;
        maxDay = (month == 12 && !PersianDateUtils.isLeapYear(year)) ? 29 : maxDay;

        if (day < 1 || day > maxDay) {
            throw new IllegalArgumentException(
                    String.format("Day must be between 1 and %d for month %d", maxDay, month)
            );
        }
    }
}