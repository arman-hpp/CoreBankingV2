package com.bank.utils.date;

import java.time.LocalDate;

/**
 * Provides static methods to convert between Gregorian and Jalali (Persian) calendar systems.
 */
public final class PersianDateConverter {
    /**
     * Converts Gregorian date to Jalali (Persian) date
     * Original algorithm by JDF.SCR.IR (<a href="http://jdf.scr.ir/jdf">...</a>)
     * License: GNU/LGPL Open Source & Free
     */
    public static PersianDateValue gregorianToJalali(int gy, int gm, int gd) {
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

        return new PersianDateValue(out[0], out[1], out[2]);
    }

    /**
     * Converts Jalali (Persian) date to Gregorian date
     * Original algorithm by JDF.SCR.IR (<a href="http://jdf.scr.ir/jdf">...</a>)
     * License: GNU/LGPL Open Source & Free
     */
    public static LocalDate jalaliToGregorian(int jy, int jm, int jd) {
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

        return LocalDate.of(out[0], out[1], out[2]);
    }
}
