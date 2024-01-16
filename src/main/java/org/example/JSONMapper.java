package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.CurrencyExchange;

public class JSONMapper {
    ObjectMapper objectMapper = new ObjectMapper();

    public CurrencyExchange mapToJava(String jsonStr) throws JsonProcessingException {
        return objectMapper.readValue(jsonStr, CurrencyExchange.class);
    }

    public <T> T mapToJava(String jsonStr, TypeReference<T> typeReference) throws JsonProcessingException {
        return objectMapper.readValue(jsonStr, typeReference);
    }

    public String mapToJSON(CurrencyExchange currencyExchange) throws JsonProcessingException {
        return objectMapper.writeValueAsString(currencyExchange);
    }
}