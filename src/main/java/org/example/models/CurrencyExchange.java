package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyExchange {

    @JsonProperty("code")
    private String code;

    @JsonProperty("rates")
    private List<CurrencyRate> rates;

    private double bid;
    private double ask;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<CurrencyRate> getRates() {
        return rates;
    }

    public void setRates(List<CurrencyRate> currencyRates) {
        this.rates = currencyRates;
        if (!currencyRates.isEmpty()) {
            this.bid = currencyRates.getFirst().getBuy();
            this.ask = currencyRates.getFirst().getSell();
        }
    }

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }

    @Override
    public String toString() {
        return "code='" + code + '\'' +
                ", buy=" + bid +
                ", sell=" + ask;
    }
}
