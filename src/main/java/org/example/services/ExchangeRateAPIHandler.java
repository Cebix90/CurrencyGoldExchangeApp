package org.example.services;
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

public class ExchangeRateAPIHandler {
    private final HttpClient client;
    private static final URI API_URL = URI.create("http://api.nbp.pl/api/exchangerates/");
    private final JSONMapper jsonMapper = new JSONMapper();

    public ExchangeRateAPIHandler(HttpClient client) {
        this.client = client;
    }

    public CurrencyExchange getExchangeRateSingleCurrency(String currency, String date) throws Exception {
        if (date.isEmpty()) {
            date = LocalDate.now().toString();
        }

        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "rates/C/" + currency + "/" + date + "/"))
                    .GET()
                    .build();

            HttpResponse<String> response = getHttpResponse(request);

            return handleHttpResponse(response);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("An error occurred while fetching the post: " + e.getMessage(), e);
        }
    }

    private HttpResponse<String> getHttpResponse(HttpRequest request) throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private CurrencyExchange handleHttpResponse(HttpResponse<String> response) throws JsonProcessingException {
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response Body:");
            CurrencyExchange currencyExchange = jsonMapper.mapToJava(response.body());
//            CurrencyRate currencyRate = currencyExchange.getRates().get(0);
            return currencyExchange;
        } else {
            throw new RuntimeException("Failed to fetch post. HTTP status code: " + response.statusCode());
        }
    }
}
