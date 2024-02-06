package org.currencygoldexchangeapp.services;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.handlers.ExchangeRateAPIHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
        double askAmount = roundAmount(sourceCurrencyExchange.getAsk() * amount);
        double bidAmount = roundAmount(sourceCurrencyExchange.getBid() * amount);

        updateExchangeAmounts(sourceCurrencyExchange, askAmount, bidAmount);

        return sourceCurrencyExchange;
    }

    private CurrencyExchange calculateExchangeAmountForOtherCurrency(CurrencyExchange sourceCurrencyExchange, double amount, String targetCurrency, String date) {
        CurrencyExchange targetCurrencyExchange = exchangeRateAPIHandler.getExchangeRateSingleCurrency(targetCurrency, date);
        double askAmount = roundAmount(sourceCurrencyExchange.getAsk() * amount / targetCurrencyExchange.getAsk());
        double bidAmount = roundAmount(sourceCurrencyExchange.getBid() * amount / targetCurrencyExchange.getBid());

        updateExchangeAmounts(sourceCurrencyExchange, askAmount, bidAmount);

        return sourceCurrencyExchange;
    }

    private void updateExchangeAmounts(CurrencyExchange currencyExchange, double askAmount, double bidAmount) {
        currencyExchange.setAsk(askAmount);
        currencyExchange.setBid(bidAmount);
    }
    private double roundAmount(double amount) {
        BigDecimal roundedAmount = new BigDecimal(amount).setScale(4, RoundingMode.HALF_UP);
        return roundedAmount.doubleValue();
    }
}