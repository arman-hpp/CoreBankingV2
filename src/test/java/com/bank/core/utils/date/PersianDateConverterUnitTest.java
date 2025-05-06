package com.bank.core.utils.date;

import com.bank.core.utils.date.PersianDateConverter;
import com.bank.core.utils.date.PersianDateValue;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersianDateConverterUnitTest {
    @Test
    void testGregorianToJalaliConversionAccuracy() {
        PersianDateValue jalali = PersianDateConverter.gregorianToJalali(2024, 5, 4);
        assertEquals(1403, jalali.year());
        assertEquals(2, jalali.month());
        assertEquals(15, jalali.day());
    }

    @Test
    void testJalaliToGregorianConversionAccuracy() {
        LocalDate gregorian = PersianDateConverter.jalaliToGregorian(1403, 2, 15);
        assertEquals(2024, gregorian.getYear());
        assertEquals(5, gregorian.getMonthValue());
        assertEquals(4, gregorian.getDayOfMonth());
    }

    @Test
    void testRoundTripConversionGregorianToJalaliToGregorian() {
        LocalDate original = LocalDate.of(2021, 3, 20);
        PersianDateValue jalali = PersianDateConverter.gregorianToJalali(original.getYear(), original.getMonthValue(), original.getDayOfMonth());
        LocalDate result = PersianDateConverter.jalaliToGregorian(jalali.year(), jalali.month(), jalali.day());

        assertEquals(original, result);
    }

    @Test
    void testRoundTripConversionJalaliToGregorianToJalali() {
        PersianDateValue original = new PersianDateValue(1399, 12, 30);
        LocalDate gregorian = PersianDateConverter.jalaliToGregorian(original.year(), original.month(), original.day());
        PersianDateValue result = PersianDateConverter.gregorianToJalali(gregorian.getYear(), gregorian.getMonthValue(), gregorian.getDayOfMonth());

        assertEquals(original, result);
    }

    @Test
    void testLeapYearConversion() {
        // 1399 was a leap year in Persian calendar
        PersianDateValue jalali = new PersianDateValue(1399, 12, 30);
        LocalDate gregorian = PersianDateConverter.jalaliToGregorian(jalali.year(), jalali.month(), jalali.day());
        PersianDateValue roundTrip = PersianDateConverter.gregorianToJalali(gregorian.getYear(), gregorian.getMonthValue(), gregorian.getDayOfMonth());

        assertEquals(jalali, roundTrip);
    }
}
