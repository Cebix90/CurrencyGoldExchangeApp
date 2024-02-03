package org.currencygoldexchangeapp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.datamodels.CurrencyRate;

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

    public List<CurrencyExchange> deserializeJsonToCurrencyExchangeList(String jsonStr) {
        List<CurrencyExchange> currencyExchanges = null;
        try {
            currencyExchanges = objectMapper.readValue(jsonStr, new TypeReference<List<CurrencyExchange>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return currencyExchanges;
    }



//    public <T> T deserializeJsonToCurrencyExchange(String jsonStr, TypeReference<T> typeReference) throws JsonProcessingException {
//        return objectMapper.readValue(jsonStr, typeReference);
//    }
//
//    public String mapToJSON(CurrencyExchange currencyExchange) throws JsonProcessingException {
//        return objectMapper.writeValueAsString(currencyExchange);
//    }
}