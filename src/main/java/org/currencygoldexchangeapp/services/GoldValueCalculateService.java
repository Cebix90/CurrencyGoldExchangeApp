package org.currencygoldexchangeapp.services;

import org.currencygoldexchangeapp.constants.ListOfHolidays;
import org.currencygoldexchangeapp.datamodels.GoldValue;
import org.currencygoldexchangeapp.exceptions.DataNotFoundException;
import org.currencygoldexchangeapp.handlers.GoldValueAPIHandler;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GoldValueCalculateService {
    private final GoldValueAPIHandler goldValueAPIHandler;
    private final ListOfHolidays listOfHolidays;

    public GoldValueCalculateService(GoldValueAPIHandler goldValueAPIHandler) {
        this.goldValueAPIHandler = goldValueAPIHandler;
        this.listOfHolidays = new ListOfHolidays(LocalDate.now().getYear());
    }

    public Optional<BigDecimal> calculateGainOrLoss(String startDate, String endDate) {
        try {
            List<GoldValue> goldValueList = goldValueAPIHandler.getGoldValuesForDateRange(startDate, endDate);

            if (goldValueList.isEmpty()) {
                return Optional.empty();
            }

            Optional<BigDecimal> currentPriceOptional = goldValueList.stream()
                    .map(goldValue -> BigDecimal.valueOf(goldValue.getValue()))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                        Collections.reverse(list);
                        return list.stream().findFirst();
                    }));

            if (currentPriceOptional.isEmpty()) {
                throw new RuntimeException("No gold value found for the end date.");
            }

            BigDecimal currentPrice = currentPriceOptional.get();

            LocalDate currentDate = LocalDate.parse(endDate);

            BigDecimal bestPrice;

            if(listOfHolidays.isHoliday(currentDate) || currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY || goldValueList.size() == 1) {
                bestPrice = calculateBestPriceForWeekendDay(goldValueList);
            } else {
                bestPrice = calculateBestPriceForWorkingDay(goldValueList);
            }

            return Optional.of(currentPrice.subtract(bestPrice));
        } catch (DataNotFoundException e) {
            return Optional.empty();
        }
    }

    private BigDecimal calculateBestPriceForWorkingDay(List<GoldValue> goldValueList) {
        return goldValueList.stream()
                .sorted(Comparator.comparing(GoldValue::getEffectiveDate).reversed())
                .skip(1)
                .max(Comparator.comparing(GoldValue::getValue))
                .map(goldValue -> BigDecimal.valueOf(goldValue.getValue()))
                .orElseThrow(() -> new RuntimeException("No gold value data found in the response."));
    }

    private BigDecimal calculateBestPriceForWeekendDay(List<GoldValue> goldValueList) {
        return goldValueList.stream()
                .max(Comparator.comparing(GoldValue::getValue))
                .map(goldValue -> BigDecimal.valueOf(goldValue.getValue()))
                .orElseThrow(() -> new RuntimeException("No gold value data found in the response."));
    }
}