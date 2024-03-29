package org.currencygoldexchangeapp.handlers;

import org.currencygoldexchangeapp.constants.APIConstants;
import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.exceptions.DataNotFoundException;
import org.currencygoldexchangeapp.utils.JSONMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExchangeRateAPIHandler {
    private static final Logger LOGGER = Logger.getLogger(ExchangeRateAPIHandler.class.getName());
    private final HttpClient client;
    private final JSONMapper jsonMapper = new JSONMapper();

    public ExchangeRateAPIHandler(HttpClient client) {
        this.client = client;
    }

    public CurrencyExchange getExchangeRateSingleCurrency(String currency, String date) {
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