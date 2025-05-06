package com.bank.core.utils.date;

import java.time.DayOfWeek;

@SuppressWarnings("unused")
public final class PersianDateUtils {
    // Persian weekday names starting from Monday (مطابق با DayOfWeek.getValue(): 1=Monday, ..., 7=Sunday)
    private static final String[] DAY_NAMES = {
            "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه", "شنبه", "یک‌شنبه"
    };

    // Names of Persian calendar months
    private static final String[] MONTH_NAMES = {
            "فروردین", "اردیبهشت", "خرداد",
            "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر",
            "دی", "بهمن", "اسفند"
    };

    /**
     * Gets the Persian name of a given month number.
     * @param month the month number (1–12)
     * @return name of the Persian month
     * @throws IllegalArgumentException if the month number is invalid
     */
    public static String getMonthName(int month) {
        if (month < 1 || month > 12)
            throw new IllegalArgumentException("Invalid Persian month number: " + month);

        return MONTH_NAMES[month - 1];
    }

    /**
     * Gets the numeric month value (1–12) corresponding to a given Persian month name.
     * @param monthName the name of the Persian month
     * @return the numeric month (1–12)
     * @throws IllegalArgumentException if the month name is not recognized
     */
    public static int getMonthNumber(String monthName) {
        if (monthName == null) {
            throw new IllegalArgumentException("Month name cannot be null");
        }

        for (var i = 0; i < MONTH_NAMES.length; i++) {
            if (MONTH_NAMES[i].equals(monthName))
                return i + 1;
        }

        throw new IllegalArgumentException("Invalid Persian month name: " + monthName);
    }

    /**
     * Gets the Persian weekday name corresponding to a {@link DayOfWeek}.
     * @param dayOfWeek the {@link DayOfWeek} enum (from java.time)
     * @return the Persian name of the weekday
     */
    public static String getWeekDayName(DayOfWeek dayOfWeek) {
        return DAY_NAMES[dayOfWeek.getValue() - 1];
    }

    /**
     * Checks if a Persian year is a leap year.
     * @param year the Persian year
     * @return true if it's a leap year
     */
    public static boolean isLeapYear(int year) {
        int remainder = (year + 2346) % 33;
        return remainder == 1 || remainder == 5 || remainder == 9 ||
                remainder == 13 || remainder == 17 || remainder == 22 ||
                remainder == 26 || remainder == 30;
    }

    /**
     * Returns the maximum valid day for a given Persian year and month.
     *
     * @param year  the Persian year
     * @param month the Persian month (1 through 12)
     * @return the maximum valid day for the specified month and year
     */
    public static int getMaxDayOfMonth(int year, int month) {
        if (month <= 6)
            return 31;
        else if (month <= 11)
            return 30;
        else
            return isLeapYear(year) ? 30 : 29;
    }
}