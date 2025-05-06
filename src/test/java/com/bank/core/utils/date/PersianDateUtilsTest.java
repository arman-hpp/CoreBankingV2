package com.bank.core.utils.date;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersianDateUtilsTest {
    @Test
    void testGetWeekDayName() {
        assertEquals("شنبه", PersianDateUtils.getWeekDayName(DayOfWeek.SATURDAY));
        assertEquals("دوشنبه", PersianDateUtils.getWeekDayName(DayOfWeek.MONDAY));
    }

    @Test
    void testGetMonthName() {
        assertEquals("فروردین", PersianDateUtils.getMonthName(1));
        assertEquals("اردیبهشت", PersianDateUtils.getMonthName(2));
        assertEquals("اسفند", PersianDateUtils.getMonthName(12));
    }

    @Test
    void testInvalidMonthThrows() {
        assertThrows(IllegalArgumentException.class, () -> PersianDateUtils.getMonthName(0));
        assertThrows(IllegalArgumentException.class, () -> PersianDateUtils.getMonthName(13));
    }
}
