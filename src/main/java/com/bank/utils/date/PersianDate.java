package com.bank.utils.date;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a date in the Persian (Jalali) calendar system.
 */
@SuppressWarnings("unused")
public final class PersianDate implements Serializable, Comparable<PersianDate> {
    @Serial
    private static final long serialVersionUID = 1L;

    private final LocalDate date;

    /**
     * Creates a PersianDate instance with the minimum possible date.
     */
    public PersianDate() {
        date = LocalDate.MIN;
    }

    /**
     * Creates a PersianDate instance from a Gregorian LocalDate.
     * @param gregorianDate the Gregorian date to convert
     */
    public PersianDate(LocalDate gregorianDate) {
        date = LocalDate.of(gregorianDate.getYear(), gregorianDate.getMonthValue(), gregorianDate.getDayOfMonth());
    }

    /**
     * Creates a PersianDate instance from Persian year, month and day.
     * @param year the Persian year (1..9999)
     * @param month the Persian month (1..12)
     * @param day the Persian day (1..31)
     * @throws IllegalArgumentException if the date is invalid
     */
    public PersianDate(int year, int month, int day) {
        validatePersianDate(year, month, day);
        var gregorianDate = jalaliToGregorian(year, month, day);
        date = LocalDate.of(gregorianDate[0], gregorianDate[1], gregorianDate[2]);
    }

    private void validatePersianDate(int year, int month, int day) {
        if (year < 1 || year > 9999) {
            throw new IllegalArgumentException("Year must be positive");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        int maxDay = (month < 7) ? 31 : 30;
        maxDay = (month == 12 && !isLeapYear(year)) ? 29 : maxDay;

        if (day < 1 || day > maxDay) {
            throw new IllegalArgumentException(
                    String.format("Day must be between 1 and %d for month %d", maxDay, month)
            );
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        PersianDate that = (PersianDate) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        return toString("d");
    }

    @Override
    public int compareTo(PersianDate o) {
        return date.compareTo(o.date);
    }

    /**
     * Formats the Persian date according to the specified pattern.
     * @param format the format pattern
     * @return formatted date string
     */
    public String toString(String format) {
        var jalaliDate = gregorianToJalali(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        var dayOfWeek = PersianDateResource.getWeekDayName(date.getDayOfWeek());
        var monthName = PersianDateResource.getMonthName(jalaliDate[1]);
        return PersianDateFormat.format(jalaliDate[0], jalaliDate[1], jalaliDate[2], dayOfWeek, monthName, format);
    }

    /**
     * Converts the Persian date to an integer in yyyyMMdd format.
     * @return the date as an integer
     */
    public int toInteger() {
        var jalaliDate = gregorianToJalali(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        return Integer.parseInt(PersianDateFormat.format(jalaliDate));
    }

    /**
     * Converts this PersianDate to a LocalDate in Gregorian calendar.
     * @return Gregorian LocalDate
     */
    public LocalDate toLocalDate() {
        return LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /**
     * Gets the current Persian date.
     * @return current Persian date
     */
    public static PersianDate now() {
        return new PersianDate(LocalDate.now());
    }

    /**
     * Converts Gregorian date to Jalali (Persian) date
     * Original algorithm by JDF.SCR.IR (<a href="http://jdf.scr.ir/jdf">...</a>)
     * License: GNU/LGPL Open Source & Free
     */
    private static int[] gregorianToJalali(int gy, int gm, int gd) {
        int[] out = {
                (gm > 2) ? (gy + 1) : gy,
                0,
                0
        };

        {
            int[] g_d_m = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
            out[2] = 355666 + (365 * gy) + ((out[0] + 3) / 4) - ((out[0] + 99) / 100)
                    + ((out[0] + 399) / 400) + gd + g_d_m[gm - 1];
        }

        out[0] = -1595 + (33 * (out[2] / 12053));
        out[2] %= 12053;
        out[0] += 4 * (out[2] / 1461);
        out[2] %= 1461;
        if (out[2] > 365) {
            out[0] += (out[2] - 1) / 365;
            out[2] = (out[2] - 1) % 365;
        }
        if (out[2] < 186) {
            out[1] = 1 + (out[2] / 31);
            out[2] = 1 + (out[2] % 31);
        } else {
            out[1] = 7 + ((out[2] - 186) / 30);
            out[2] = 1 + ((out[2] - 186) % 30);
        }

        return out;
    }

    /**
     * Converts Jalali (Persian) date to Gregorian date
     * Original algorithm by JDF.SCR.IR (<a href="http://jdf.scr.ir/jdf">...</a>)
     * License: GNU/LGPL Open Source & Free
     */
    private static int[] jalaliToGregorian(int jy, int jm, int jd) {
        jy += 1595;
        int[] out = {
                0,
                0,
                -355668 + (365 * jy) + ((jy / 33) * 8) + (((jy % 33) + 3) / 4) + jd + (
                        (jm < 7) ? (jm - 1) * 31 : ((jm - 7) * 30) + 186)
        };

        out[0] = 400 * (out[2] / 146097);
        out[2] %= 146097;
        if (out[2] > 36524) {
            out[0] += 100 * (--out[2] / 36524);
            out[2] %= 36524;
            if (out[2] >= 365) {
                out[2]++;
            }
        }

        out[0] += 4 * (out[2] / 1461);
        out[2] %= 1461;
        if (out[2] > 365) {
            out[0] += (out[2] - 1) / 365;
            out[2] = (out[2] - 1) % 365;
        }

        int[] sal_a = {0, 31, ((out[0] % 4 == 0 && out[0] % 100 != 0) || (out[0] % 400 == 0)) ? 29 : 28,
                31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        for (out[2]++; out[1] < 13 && out[2] > sal_a[out[1]]; out[1]++) {
            out[2] -= sal_a[out[1]];
        }

        return out;
    }
}
