package org.currencygoldexchangeapp.constants;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ListOfHolidays {
    public final List<LocalDate> holidays;


    public ListOfHolidays(int year) {
        this.holidays = Arrays.asList(
                LocalDate.of(year, 1, 1),
                LocalDate.of(year, 1, 6),
                calculateEasterMonday(year),
                LocalDate.of(year, 4, 1),
                LocalDate.of(year, 5, 1),
                LocalDate.of(year, 5, 3),
                LocalDate.of(year, 5, 19),
                LocalDate.of(year, 5, 30),
                LocalDate.of(year, 8, 15),
                LocalDate.of(year, 11, 1),
                LocalDate.of(year, 11, 11),
                LocalDate.of(year, 12, 25),
                LocalDate.of(year, 12, 26)
        );
    }

    public boolean contains(LocalDate date) {
        return holidays.contains(date);
    }

    private LocalDate calculateEasterMonday(int yearInput){
        int a, b, c, d, e, f, g, h, i ,j, k, m, p;
        int month, day, year;

        year = yearInput;

        a = year % 19;
        b = year / 100;
        c = year % 100;
        d = b / 4;
        e = b % 4;
        f = (8 + b) / 25;
        g = (b - f + 1) / 3;
        h = (19 * a + b - d - g + 15) % 30;
        i = c / 4;
        k = c % 4;
        j = (32 + 2 * e + 2 * i - h- k) % 7;
        m = (a + 11 * h + 22 * j) / 451;
        month = (h + j - 7 * m + 114) / 31;
        p = (h + j - 7 * m + 114) % 31;
        day = p + 1;

        if(month == 3 && day == 31){
            month = 4;
            day = 1;
        } else {
            day += 1;
        }

        return LocalDate.of(year, month, day);
    }
}