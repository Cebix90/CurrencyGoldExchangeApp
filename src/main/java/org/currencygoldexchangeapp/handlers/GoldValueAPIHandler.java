package org.currencygoldexchangeapp.handlers;

import org.currencygoldexchangeapp.constants.APIConstants;
import org.currencygoldexchangeapp.datamodels.GoldValue;
import org.currencygoldexchangeapp.exceptions.DataNotFoundException;
import org.currencygoldexchangeapp.exceptions.ExceededResultsLimitException;
import org.currencygoldexchangeapp.utils.JSONMapper;

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

public class GoldValueAPIHandler {
    private static final Logger LOGGER = Logger.getLogger(ExchangeRateAPIHandler.class.getName());
    private final HttpClient client;
    private final JSONMapper jsonMapper = new JSONMapper();

    public GoldValueAPIHandler(HttpClient client) {
        this.client = client;
    }

    public GoldValue getGoldValueForSpecificDate(String date) {
        if(date == null){
            throw new DataNotFoundException();
        }

        if (date.isEmpty()) {
            date = LocalDate.now().toString();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APIConstants.GOLD_VALUE_API_URL + date))
                .GET()
                .build();

        HttpResponse<String> response = getHttpResponse(request);

        return handleHttpResponse(response);
    }

    public List<GoldValue> getGoldValuesForDateRange(String startDate, String endDate) {
        if(startDate == null || endDate == null){
            throw new DataNotFoundException();
        }

        if(startDate.isEmpty() || endDate.isEmpty()){
            throw new DataNotFoundException();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APIConstants.GOLD_VALUE_API_URL + startDate + "/" + endDate))
                .GET()
                .build();

        HttpResponse<String> response = getHttpResponse(request);

        return handleHttpResponseForList(response);
    }

    private HttpResponse<String> getHttpResponse(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while making the HTTP request.", e);

            throw new RuntimeException("An error occurred while making the HTTP request.", e);
        }
    }

    private GoldValue handleHttpResponse(HttpResponse<String> response) {
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            List<GoldValue> goldValues = jsonMapper.deserializeJsonToGoldValueList(response.body());
            if (!goldValues.isEmpty()) {
                return goldValues.getFirst();
            } else {
                throw new RuntimeException("No gold value data found in the response.");
            }
        } else if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new DataNotFoundException();
        } else if (response.statusCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
            throw new ExceededResultsLimitException();
        } else {
            throw new RuntimeException("Failed to fetch post. HTTP status code: " + response.statusCode());
        }
    }

    private List<GoldValue> handleHttpResponseForList(HttpResponse<String> response) {
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            List<GoldValue> goldValues = jsonMapper.deserializeJsonToGoldValueList(response.body());
            if (!goldValues.isEmpty()) {
                return goldValues;
            } else {
                throw new RuntimeException("No gold value data found in the response.");
            }
        } else if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new DataNotFoundException();
        } else if (response.statusCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
            throw new ExceededResultsLimitException();
        } else {
            throw new RuntimeException("Failed to fetch post. HTTP status code: " + response.statusCode());
        }
    }
}
