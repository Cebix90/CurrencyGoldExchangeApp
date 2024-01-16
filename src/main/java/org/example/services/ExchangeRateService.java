package org.example.services;

import org.example.models.CurrencyExchange;

public class ExchangeRateService {
    private final EchangeRateAPIHandler echangeRateAPIHandler;

    public ExchangeRateService() {
        this.echangeRateAPIHandler = new EchangeRateAPIHandler();
    }

    public CurrencyExchange getExchangeRate(String currency, String date) throws Exception {
        return echangeRateAPIHandler.getExchangeRateSingleCurrency(currency, date);
    }
}
