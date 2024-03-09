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
                LocalDate.of(year, 3, 31),
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
}