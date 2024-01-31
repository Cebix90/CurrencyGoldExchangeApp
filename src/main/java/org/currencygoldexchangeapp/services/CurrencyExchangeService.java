package org.currencygoldexchangeapp.services;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.handlers.ExchangeRateAPIHandler;

public class CurrencyExchangeService {
    private final ExchangeRateAPIHandler exchangeRateAPIHandler;

    public CurrencyExchangeService(ExchangeRateAPIHandler exchangeRateAPIHandler) {
        this.exchangeRateAPIHandler = exchangeRateAPIHandler;
    }

    public CurrencyExchange calculateExchangeAmount (String sourceCurrency, double amount, String targetCurrency, String date) {
        CurrencyExchange sourceCurrencyExchange = exchangeRateAPIHandler.getExchangeRateSingleCurrency(sourceCurrency, date);

        return targetCurrency.isEmpty() || targetCurrency.equalsIgnoreCase("pln")
                ? calculateExchangeAmountForPLN(sourceCurrencyExchange, amount)
                : calculateExchangeAmountForOtherCurrency(sourceCurrencyExchange, amount, targetCurrency, date);
    }

    private CurrencyExchange getCurrencyExchange(String currency, String date) {
        CurrencyExchange currencyExchange = exchangeRateAPIHandler.getExchangeRateSingleCurrency(currency, date);
        if (currencyExchange == null) {
            throw new CurrencyNotFoundException("Unable to fetch exchange rate for currency: " + currency);
        }
        return currencyExchange;
    }

    private CurrencyExchange calculateExchangeAmountForPLN(CurrencyExchange sourceCurrencyExchange, double amount) {
        double askAmount = sourceCurrencyExchange.getAsk() * amount;
        double bidAmount = sourceCurrencyExchange.getBid() * amount;
        sourceCurrencyExchange.setAsk(askAmount);
        sourceCurrencyExchange.setBid(bidAmount);
        return sourceCurrencyExchange;
    }

    private CurrencyExchange calculateExchangeAmountForOtherCurrency(CurrencyExchange sourceCurrencyExchange, double amount, String targetCurrency, String date) {
        CurrencyExchange targetCurrencyExchange = getCurrencyExchange(targetCurrency, date);
        double askAmount = sourceCurrencyExchange.getAsk() * amount / targetCurrencyExchange.getAsk();
        double bidAmount = sourceCurrencyExchange.getBid() * amount / targetCurrencyExchange.getBid();
        sourceCurrencyExchange.setAsk(askAmount);
        sourceCurrencyExchange.setBid(bidAmount);
        return sourceCurrencyExchange;
    }
}