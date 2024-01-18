package org.example.services;

import org.example.models.CurrencyExchange;

import java.net.http.HttpClient;

public class ExchangeRateService {
    private final ExchangeRateAPIHandler exchangeRateAPIHandler;
    public ExchangeRateService() {
        this.exchangeRateAPIHandler = new ExchangeRateAPIHandler(HttpClient.newHttpClient());
    }

    public CurrencyExchange getExchangeRate(String currency, String date) throws Exception {
        return exchangeRateAPIHandler.getExchangeRateSingleCurrency(currency, date);
    }
}
