package com.bank.core.utils.date;

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

    // Underlying Gregorian date used for internal storage and comparison
    private final LocalDate date;

    /**
     * Creates a PersianDate instance with the minimum possible date.
     */
    public PersianDate() {
        this.date = LocalDate.MIN;
    }

    /**
     * Creates a PersianDate instance from a Gregorian LocalDate.
     * @param gregorianDate the Gregorian date to convert
     * @throws NullPointerException if the inputs is invalid
     */
    public PersianDate(LocalDate gregorianDate) {
        this.date = Objects.requireNonNull(gregorianDate, "gregorianDate must not be null");
    }

    /**
     * Creates a PersianDate instance from Persian year, month and day.
     * @param year the Persian year (1..9999)
     * @param month the Persian month (1..12)
     * @param dayOfMonth the Persian day (1..31)
     * @throws IllegalArgumentException if the inputs is invalid
     */
    public PersianDate(int year, int month, int dayOfMonth) {
        PersianDateValidator.validate(year, month, dayOfMonth);
        this.date = PersianDateConverter.jalaliToGregorian(year, month, dayOfMonth);
    }

    /**
     * Static factory method to create a PersianDate from year, month, and day.
     */
    public static PersianDate of(int year, int month, int dayOfMonth) {
        return new PersianDate(year, month, dayOfMonth);
    }

    /**
     * Checks equality based on the internal Gregorian date.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        PersianDate that = (PersianDate) o;
        return Objects.equals(date, that.date);
    }

    /**
     * Returns hash code based on the internal date.
     */
    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    /**
     * Returns string representation using default format.
     */
    @Override
    public String toString() {
        return toString("d");
    }

    /**
     * Compares PersianDate instances based on the internal date.
     */
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
        var jalaliDate = getJalaliDate();
        var dayOfWeek = PersianDateUtils.getWeekDayName(date.getDayOfWeek());
        var monthName = PersianDateUtils.getMonthName(jalaliDate.month());
        return PersianDateFormat.format(jalaliDate.year(), jalaliDate.month(), jalaliDate.day(),
                dayOfWeek, monthName, format);
    }

    /**
     * Converts the Persian date to an integer in yyyyMMdd format.
     * Useful for sorting, comparisons, and database keys.
     * @return the date as an integer
     */
    public int toInteger() {
        var jalaliDate = getJalaliDate();
        var formatted = PersianDateFormat.format(jalaliDate);
        return Integer.parseInt(formatted);
    }

    // Helper to reduce code duplication
    private PersianDateValue getJalaliDate() {
        return PersianDateConverter.gregorianToJalali(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /**
     * Converts this PersianDate to a LocalDate in Gregorian calendar.
     * @return Gregorian LocalDate
     */
    public LocalDate toLocalDate() {
        return date;
    }

    /**
     * Gets the current Persian date.
     * @return current Persian date
     */
    public static PersianDate now() {
        return new PersianDate(LocalDate.now());
    }

    /**
     * Returns the Persian year part of this date.
     * @return Persian year
     */
    public int getYear() {
        return getJalaliDate().year();
    }

    /**
     * Returns the Persian month part of this date.
     * @return Persian month
     */
    public int getMonth() {
        return getJalaliDate().month();
    }

    /**
     * Returns the Persian day of month part of this date.
     * @return Persian day of month
     */
    public int getDay() {
        return getJalaliDate().day();
    }
}
