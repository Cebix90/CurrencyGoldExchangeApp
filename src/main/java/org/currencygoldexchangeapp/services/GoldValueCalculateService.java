package org.currencygoldexchangeapp.services;

import org.currencygoldexchangeapp.datamodels.GoldValue;
import org.currencygoldexchangeapp.handlers.GoldValueAPIHandler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class GoldValueCalculateService {
    private final GoldValueAPIHandler goldValueAPIHandler;

    public GoldValueCalculateService(GoldValueAPIHandler goldValueAPIHandler) {
        this.goldValueAPIHandler = goldValueAPIHandler;
    }

    public BigDecimal calculateGainOrLoss(String startDate, String endDate) {
        List<GoldValue> goldValueList = goldValueAPIHandler.getGoldValuesForDateRange(startDate, endDate);

        List<BigDecimal> goldValueListConvertedToBigDecimal = goldValueList.stream()
                .filter(d -> d.getEffectiveDate().equals(LocalDate.now().toString()))
                .map(goldValue -> BigDecimal.valueOf(goldValue.getValue()))
                .toList();

        BigDecimal currentPrice = goldValueListConvertedToBigDecimal.getFirst();

        BigDecimal bestPrice = calculateBestPrice(goldValueList);

        return currentPrice.subtract(bestPrice);
    }

    private BigDecimal calculateBestPrice(List<GoldValue> goldValueList) {
        return goldValueList.stream()
                .max(Comparator.comparing(GoldValue::getValue))
                .map(goldValue -> BigDecimal.valueOf(goldValue.getValue()))
                .orElseThrow(() -> new RuntimeException("No gold value data found in the response."));
    }
}
