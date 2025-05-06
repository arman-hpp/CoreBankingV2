package com.bank.core.utils.date;

/**
 * A utility class for formatting Persian dates in various formats.
 */
public final class PersianDateFormat {
    /**
     * Formats a PersianDateValue object into a string representation.
     * Uses default formatting (compact numeric format: yyyyMMdd).
     *
     * @param persianDateValue The Persian date value to format
     * @return Formatted date string
     */
    public static String format(PersianDateValue persianDateValue) {
        return format(persianDateValue.year(), persianDateValue.month(), persianDateValue.day(), "", "", "");
    }

    /**
     * Formats Persian date components into a string representation based on the specified format.
     *
     * @param year The year component
     * @param month The month component
     * @param day The day component
     * @param dayOfWeek The name of the day of week (used in "D" format)
     * @param monthName The name of the month (used in "D" format)
     * @param format The format specifier:
     *               - "d": year/month/day (e.g. 1402/3/15)
     *               - "D": full format with names (e.g. Saturday 15 Ordibehesht 1402)
     *               - "is": ISO-like format with leading zeros (e.g. 1402/03/15)
     *               - default: compact numeric format (e.g. 14020315)
     * @return Formatted date string according to the specified format
     */
    public static String format(int year, int month, int day, String dayOfWeek, String monthName, String format) {
        return switch (format) {
            case "d" -> String.format("%d/%d/%d", year, month, day);
            case "D" -> String.format("%s %d %s %d", dayOfWeek, day, monthName, year);
            case "is" -> String.format("%02d/%02d/%02d", year, month, day);
            default -> String.format("%04d%02d%02d", year, month, day);
        };
    }
}
