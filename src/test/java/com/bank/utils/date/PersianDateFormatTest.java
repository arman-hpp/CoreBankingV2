package com.bank.utils.date;

import com.bank.core.utils.date.PersianDateFormat;
import com.bank.core.utils.date.PersianDateValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersianDateFormatTest {
    @Test
    void testDefaultFormatFromValue() {
        var date = new PersianDateValue(1402, 7, 3);
        String result = PersianDateFormat.format(date);
        assertEquals("14020703", result);
    }

    @Test
    void testFullFormatWithDayAndMonthName() {
        String formatted = PersianDateFormat.format(
                1403, 2, 15, "شنبه", "اردیبهشت", "D"
        );
        assertEquals("شنبه 15 اردیبهشت 1403", formatted);
    }

    @Test
    void testNumericFormat() {
        String formatted = PersianDateFormat.format(
                1399, 12, 30, "شنبه", "اسفند", "d"
        );
        assertEquals("1399/12/30", formatted);
    }
}
