package org.currencygoldexchangeapp;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.handlers.ExchangeRateAPIHandler;

import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter currency code: ");
            String currencyCode = scanner.next();

            System.out.print("Enter date (optional, press ENTER for today's date): ");
            scanner.nextLine();
            String dateString = scanner.nextLine();
            LocalDate date = dateString.isEmpty() ? LocalDate.now() : LocalDate.parse(dateString);

            ExchangeRateAPIHandler exchangeRateAPIHandler = new ExchangeRateAPIHandler(HttpClient.newHttpClient());
            CurrencyExchange exchangeRateResult = exchangeRateAPIHandler.getExchangeRateSingleCurrency(currencyCode, date.toString());

            System.out.println("Exchange rate for " + currencyCode + " on " + date + ": " + exchangeRateResult);

        } catch (DateTimeParseException e) {
            System.out.println("Error during date parsing: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Another error: " + e.getMessage());
        }
    }
}