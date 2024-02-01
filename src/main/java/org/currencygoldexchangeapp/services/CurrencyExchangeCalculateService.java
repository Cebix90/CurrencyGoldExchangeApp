package org.currencygoldexchangeapp.services;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.handlers.ExchangeRateAPIHandler;

public class CurrencyExchangeCalculateService {
    private final ExchangeRateAPIHandler exchangeRateAPIHandler;

    public CurrencyExchangeCalculateService(ExchangeRateAPIHandler exchangeRateAPIHandler) {
        this.exchangeRateAPIHandler = exchangeRateAPIHandler;
    }

    public CurrencyExchange calculateExchangeAmount (String sourceCurrency, double amount, String targetCurrency, String date) {
        if (sourceCurrency == null) {
            throw new IllegalArgumentException("Source currency must not be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number");
        }

        CurrencyExchange sourceCurrencyExchange = exchangeRateAPIHandler.getExchangeRateSingleCurrency(sourceCurrency, date);

        return targetCurrency.isEmpty() || targetCurrency.equalsIgnoreCase("pln")
                ? calculateExchangeAmountForPLN(sourceCurrencyExchange, amount)
                : calculateExchangeAmountForOtherCurrency(sourceCurrencyExchange, amount, targetCurrency, date);
    }

    private CurrencyExchange calculateExchangeAmountForPLN(CurrencyExchange sourceCurrencyExchange, double amount) {
        double askAmount = sourceCurrencyExchange.getAsk() * amount;
        double bidAmount = sourceCurrencyExchange.getBid() * amount;
        sourceCurrencyExchange.setAsk(askAmount);
        sourceCurrencyExchange.setBid(bidAmount);
        return sourceCurrencyExchange;
    }

    private CurrencyExchange calculateExchangeAmountForOtherCurrency(CurrencyExchange sourceCurrencyExchange, double amount, String targetCurrency, String date) {
        CurrencyExchange targetCurrencyExchange = exchangeRateAPIHandler.getExchangeRateSingleCurrency(targetCurrency, date);
        double askAmount = sourceCurrencyExchange.getAsk() * amount / targetCurrencyExchange.getAsk();
        double bidAmount = sourceCurrencyExchange.getBid() * amount / targetCurrencyExchange.getBid();

        askAmount = Math.round(askAmount * 10000.0) / 10000.0;
        bidAmount = Math.round(bidAmount * 10000.0) / 10000.0;

        sourceCurrencyExchange.setAsk(askAmount);
        sourceCurrencyExchange.setBid(bidAmount);

        return sourceCurrencyExchange;
    }
}