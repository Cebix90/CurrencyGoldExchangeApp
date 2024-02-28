package org.currencygoldexchangeapp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.datamodels.CurrencyRate;
import org.currencygoldexchangeapp.datamodels.GoldValue;

import java.util.List;

public class JSONMapper {
    ObjectMapper objectMapper = new ObjectMapper();

    public CurrencyExchange deserializeJsonToCurrencyExchange(String jsonStr) {
        CurrencyExchange currencyExchange = null;
        try {
            currencyExchange = objectMapper.readValue(jsonStr, CurrencyExchange.class);
            List<CurrencyRate> currencyRates = currencyExchange.getRates();
            if (!currencyRates.isEmpty()) {
                currencyExchange.setBid(currencyRates.getFirst().getBid());
                currencyExchange.setAsk(currencyRates.getFirst().getAsk());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return currencyExchange;
    }

    public List<GoldValue> deserializeJsonToGoldValueList(String jsonStr) {
        try {
            return objectMapper.readValue(jsonStr, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}