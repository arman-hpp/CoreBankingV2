package com.bank.utils.date;

import java.time.LocalDate;

/**
 * Provides static methods to convert between Gregorian and Jalali (Persian) calendar systems.
 * Original algorithm by JDF.SCR.IR (<a href="http://jdf.scr.ir/jdf">...</a>)
 * License: GNU/LGPL Open Source & Free
 */
public final class PersianDateConverter {
    private static final int GREGORIAN_EPOCH_OFFSET = -355668; // Offset for Gregorian epoch days
    private static final int JALALI_YEAR_OFFSET = 1595; // Offset for Jalali year calculation
    private static final int GREGORIAN_CYCLE_DAYS = 146097; // Days in a 400-year Gregorian cycle
    private static final int GREGORIAN_CENTURY_DAYS = 36524; // Days in a 100-year Gregorian cycle
    private static final int GREGORIAN_4YEAR_CYCLE = 1461; // Days in a 4-year Gregorian cycle
    private static final int GREGORIAN_EPOCH = 355666; // Days to Gregorian epoch
    private static final int JALALI_CYCLE_DAYS = 12053; // Days in a Jalali cycle
    private static final int[] GREGORIAN_CUMULATIVE_MONTH_DAYS = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};

    /**
     * Converts a Gregorian date to a Jalali (Persian) date.
     * @param gregorianYear  the Gregorian year (e.g., 2023)
     * @param gregorianMonth the Gregorian month (1-12)
     * @param gregorianDay   the Gregorian day (1-31)
     * @return a {@link PersianDateValue} representing the equivalent Jalali date
     */
    public static PersianDateValue gregorianToJalali(int gregorianYear, int gregorianMonth, int gregorianDay) {
        // Step 1: Calculate total days since epoch
        var baseYear = (gregorianMonth > 2) ? (gregorianYear + 1) : gregorianYear;
        var totalDays = GREGORIAN_EPOCH + (365 * gregorianYear) + ((baseYear + 3) / 4) - ((baseYear + 99) / 100)
                + ((baseYear + 399) / 400) + gregorianDay + GREGORIAN_CUMULATIVE_MONTH_DAYS[gregorianMonth - 1];

        // Step 2: Convert to Jalali year
        var jalaliYear = (33 * (totalDays / JALALI_CYCLE_DAYS)) - JALALI_YEAR_OFFSET;
        totalDays %= JALALI_CYCLE_DAYS;

        // Step 3: Adjust for 4-year cycles
        jalaliYear += 4 * (totalDays / GREGORIAN_4YEAR_CYCLE);
        totalDays %= GREGORIAN_4YEAR_CYCLE;

        // Step 4: Handle remaining days
        if (totalDays > 365) {
            jalaliYear += (totalDays - 1) / 365;
            totalDays = (totalDays - 1) % 365;
        }

        // Step 5: Calculate month and day
        int jalaliMonth, jalaliDay;
        if (totalDays < 186) {
            jalaliMonth = 1 + (totalDays / 31);
            jalaliDay = 1 + (totalDays % 31);
        } else {
            totalDays -= 186;
            jalaliMonth = 7 + (totalDays / 30);
            jalaliDay = 1 + (totalDays % 30);
        }

        return new PersianDateValue(jalaliYear, jalaliMonth, jalaliDay);
    }

    /**
     * Converts a Jalali (Persian) date to a Gregorian date.
     *
     * @param jalaliYear  the Jalali year (e.g., 1402)
     * @param jalaliMonth the Jalali month (1-12)
     * @param jalaliDay   the Jalali day (1-31)
     * @return a {@link LocalDate} representing the equivalent Gregorian date
     */
    public static LocalDate jalaliToGregorian(int jalaliYear, int jalaliMonth, int jalaliDay) {
        // Step 1: Calculate total days since epoch
        var adjustedYear = jalaliYear + JALALI_YEAR_OFFSET;
        var totalDays = GREGORIAN_EPOCH_OFFSET + (365 * adjustedYear) + ((adjustedYear / 33) * 8)
                + (((adjustedYear % 33) + 3) / 4) + jalaliDay
                + (jalaliMonth < 7 ? (jalaliMonth - 1) * 31 : ((jalaliMonth - 7) * 30) + 186);

        // Step 2: Calculate Gregorian year
        var gregorianYear = 400 * (totalDays / GREGORIAN_CYCLE_DAYS);
        totalDays %= GREGORIAN_CYCLE_DAYS;
        if (totalDays > GREGORIAN_CENTURY_DAYS) {
            gregorianYear += 100 * (--totalDays / GREGORIAN_CENTURY_DAYS);
            totalDays %= GREGORIAN_CENTURY_DAYS;
            if (totalDays >= 365) {
                totalDays++;
            }
        }
        gregorianYear += 4 * (totalDays / GREGORIAN_4YEAR_CYCLE);
        totalDays %= GREGORIAN_4YEAR_CYCLE;
        if (totalDays > 365) {
            gregorianYear += (totalDays - 1) / 365;
            totalDays = (totalDays - 1) % 365;
        }

        // Step 3: Calculate Gregorian month and day
        int[] monthDays  = {0, 31, ((gregorianYear % 4 == 0 && gregorianYear % 100 != 0) || (gregorianYear % 400 == 0)) ? 29 : 28,
                31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int gregorianMonth = 0;
        for (totalDays++; gregorianMonth < 12 && totalDays > monthDays[gregorianMonth + 1]; gregorianMonth++) {
            totalDays -= monthDays[gregorianMonth + 1];
        }
        gregorianMonth = gregorianMonth + 1;

        return LocalDate.of(gregorianYear, gregorianMonth, totalDays);
    }
}
