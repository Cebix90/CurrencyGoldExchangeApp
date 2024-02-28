package org.currencygoldexchangeapp.datamodels;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoldValue {
    @JsonProperty("data")
    private String effectiveDate;

    @JsonProperty("cena")
    private double value;

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
