package org.currencygoldexchangeapp.handlers;

import org.currencygoldexchangeapp.constants.APIConstants;
import org.currencygoldexchangeapp.exceptions.CurrencyNotFoundException;
import org.currencygoldexchangeapp.exceptions.DataNotFoundException;
import org.currencygoldexchangeapp.utils.JSONMapper;
import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.datamodels.CurrencyRate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExchangeRateAPIHandler {
    private static final Logger LOGGER = Logger.getLogger(ExchangeRateAPIHandler.class.getName());
    private final HttpClient client;
    private final JSONMapper jsonMapper = new JSONMapper();

    public ExchangeRateAPIHandler(HttpClient client) {
        this.client = client;
    }

    public CurrencyExchange getExchangeRateSingleCurrency(String currency, String date) {
        if (!isCurrencyAvailable(currency, date)) {
            throw new CurrencyNotFoundException("The requested currency " + currency + " is not available.");
        }

        if (date.isEmpty()) {
            date = LocalDate.now().toString();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APIConstants.EXCHANGE_RATE_API_URL + "rates/C/" + currency + "/" + date + "/"))
                .GET()
                .build();

        HttpResponse<String> response = getHttpResponse(request);

        return handleHttpResponse(response);
    }

    public List<String> getAvailableCurrencies(String date) {
        if (date.isEmpty()) {
            date = LocalDate.now().toString();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APIConstants.EXCHANGE_RATE_API_URL + "tables/C/" + date + "/"))
                .GET()
                .build();

        HttpResponse<String> response = getHttpResponse(request);

        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            List<CurrencyExchange> currencyExchanges = jsonMapper.deserializeJsonToCurrencyExchangeList(response.body());
            return currencyExchanges.stream()
                    .flatMap(currencyExchange -> currencyExchange.getRates().stream())
                    .map(CurrencyRate::getCode)
                    .collect(Collectors.toList());
        } else if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new DataNotFoundException();
        } else {
            throw new RuntimeException("Failed to fetch available currencies. HTTP status code: " + response.statusCode());
        }
    }

    public boolean isCurrencyAvailable(String currency, String date) {
        List<String> availableCurrencies = getAvailableCurrencies(date);
        return availableCurrencies.stream()
                .map(String::toLowerCase)
                .toList()
                .contains(currency.toLowerCase());
    }

    private HttpResponse<String> getHttpResponse(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while making the HTTP request.", e);

            throw new RuntimeException("An error occurred while making the HTTP request.", e);
        }
    }

    private CurrencyExchange handleHttpResponse(HttpResponse<String> response) {
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            return jsonMapper.deserializeJsonToCurrencyExchange(response.body());
        } else if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new DataNotFoundException();
        } else {
            throw new RuntimeException("Failed to fetch post. HTTP status code: " + response.statusCode());
        }
    }
}