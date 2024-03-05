package org.currencygoldexchangeapp.services;

import org.currencygoldexchangeapp.datamodels.GoldValue;
import org.currencygoldexchangeapp.handlers.GoldValueAPIHandler;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class GoldValueCalculateService {
    private final GoldValueAPIHandler goldValueAPIHandler;

    public GoldValueCalculateService(GoldValueAPIHandler goldValueAPIHandler) {
        this.goldValueAPIHandler = goldValueAPIHandler;
    }

    public BigDecimal calculateGainOrLoss(BigDecimal currentPrice, BigDecimal bestPrice) {
        return currentPrice.subtract(bestPrice);
    }

    public BigDecimal calculateBestPrice(int days) {
        List<GoldValue> goldValues = goldValueAPIHandler.getLastGoldValues(days);
        return goldValues.stream()
                .max(Comparator.comparing(GoldValue::getValue))
                .map(goldValue -> BigDecimal.valueOf(goldValue.getValue()))
                .orElseThrow(() -> new RuntimeException("No gold value data found in the response."));
    }
}
