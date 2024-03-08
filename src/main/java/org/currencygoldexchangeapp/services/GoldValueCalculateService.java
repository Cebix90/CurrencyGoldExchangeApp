package org.currencygoldexchangeapp.services;

import org.currencygoldexchangeapp.datamodels.GoldValue;
import org.currencygoldexchangeapp.handlers.GoldValueAPIHandler;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GoldValueCalculateService {
    private final GoldValueAPIHandler goldValueAPIHandler;

    public GoldValueCalculateService(GoldValueAPIHandler goldValueAPIHandler) {
        this.goldValueAPIHandler = goldValueAPIHandler;
    }

    public BigDecimal calculateGainOrLoss(String startDate, String endDate) {
        List<GoldValue> goldValueList = goldValueAPIHandler.getGoldValuesForDateRange(startDate, endDate);

        Optional<BigDecimal> currentPriceOptional = goldValueList.stream()
                .filter(d -> d.getEffectiveDate().equals(endDate))
                .map(goldValue -> BigDecimal.valueOf(goldValue.getValue()))
                .findFirst();

        if (currentPriceOptional.isEmpty()) {
            throw new RuntimeException("No gold value found for the end date.");
        }

        BigDecimal currentPrice = currentPriceOptional.get();

        BigDecimal bestPrice = calculateBestPrice(goldValueList);

        return currentPrice.subtract(bestPrice);
    }

    private BigDecimal calculateBestPrice(List<GoldValue> goldValueList) {
        return goldValueList.stream()
                .sorted(Comparator.comparing(GoldValue::getEffectiveDate).reversed())
                .skip(1)
                .max(Comparator.comparing(GoldValue::getValue))
                .map(goldValue -> BigDecimal.valueOf(goldValue.getValue()))
                .orElseThrow(() -> new RuntimeException("No gold value data found in the response."));
    }
}
