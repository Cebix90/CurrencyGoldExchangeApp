package org.example.handlers;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.JSONMapper;
import org.example.models.CurrencyExchange;

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
    private static final Logger logger = Logger.getLogger(ExchangeRateAPIHandler.class.getName());

    private final HttpClient client;
    private static final URI API_URL = URI.create("https://api.nbp.pl/api/exchangerates/");
    private final JSONMapper jsonMapper = new JSONMapper();

    public ExchangeRateAPIHandler(HttpClient client) {
        this.client = client;
    }

    public CurrencyExchange getExchangeRateSingleCurrency(String currency, String date) {
        if (date.isEmpty()) {
            date = LocalDate.now().toString();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "rates/C/" + currency + "/" + date + "/"))
                .GET()
                .build();

        HttpResponse<String> response = getHttpResponse(request);

        return handleHttpResponse(response);
    }

    private HttpResponse<String> getHttpResponse(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "An error occurred while making the HTTP request.", e);

            throw new RuntimeException("An error occurred while making the HTTP request.", e);
        }
    }

    private CurrencyExchange handleHttpResponse(HttpResponse<String> response) {
        try{
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Status code: " + response.statusCode());
                System.out.println("Response Body:");
                return jsonMapper.deserializeJsonToCurrencyExchange(response.body());
            } else {
                throw new RuntimeException("Failed to fetch post. HTTP status code: " + response.statusCode());
            }
        } catch (JsonProcessingException e){
            logger.log(Level.SEVERE, "An error occurred while processing the JSON response.", e);

            throw new RuntimeException("An error occurred while processing the JSON response.", e);
        }
    }
}
