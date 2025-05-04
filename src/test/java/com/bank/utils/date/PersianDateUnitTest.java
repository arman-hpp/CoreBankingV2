package com.bank.utils.date;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PersianDateUnitTest {
    @Test
    void testConstructorWithValidJalaliDate() {
        PersianDate date = new PersianDate(1403, 2, 15);
        assertNotNull(date);
    }

    @Test
    void testConstructorWithGregorianDate() {
        LocalDate gregorian = LocalDate.of(2024, 5, 4);
        PersianDate date = new PersianDate(gregorian);
        assertNotNull(date);
        assertEquals(gregorian, date.toLocalDate());
    }

    @Test
    void testConstructorWithInvalidDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new PersianDate(1403, 13, 1));
        assertThrows(IllegalArgumentException.class, () -> new PersianDate(1403, 2, 32));
    }

    @Test
    void testToStringDefaultFormat() {
        PersianDate date = new PersianDate(1403, 2, 15);
        String formatted = date.toString();
        assertNotNull(formatted);
        assertFalse(formatted.isEmpty());
    }

    @Test
    void testToIntegerFormat() {
        PersianDate date = new PersianDate(1403, 2, 15);
        int intFormat = date.toInteger();
        assertEquals(14030215, intFormat);
    }

    @Test
    void testNowIsCloseToToday() {
        PersianDate now = PersianDate.now();
        LocalDate today = LocalDate.now();
        assertEquals(today, now.toLocalDate());
    }

    @Test
    void testEqualsAndHashCode() {
        PersianDate date1 = new PersianDate(1403, 2, 15);
        PersianDate date2 = new PersianDate(date1.toLocalDate());

        assertEquals(date1, date2);
        assertEquals(date1.hashCode(), date2.hashCode());
    }

    @Test
    void testCompareTo() {
        PersianDate earlier = new PersianDate(1399, 1, 1);
        PersianDate later = new PersianDate(1402, 12, 29);
        assertTrue(earlier.compareTo(later) < 0);
    }
}
