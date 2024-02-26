package org.currencygoldexchangeapp.datamodels;

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

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public void setRates(List<CurrencyRate> currencyRates) {
        this.rates = currencyRates;
    }

    @Override
    public String toString() {
        return "bid=" + bid + ", ask=" + ask;
    }
}