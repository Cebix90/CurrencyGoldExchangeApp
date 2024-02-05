package org.currencygoldexchangeapp;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.exceptions.CurrencyNotFoundException;
import org.currencygoldexchangeapp.exceptions.DataNotFoundException;
import org.currencygoldexchangeapp.handlers.ExchangeRateAPIHandler;
import org.currencygoldexchangeapp.services.CurrencyExchangeCalculateService;

import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            LocalDate date = getDate(scanner);

            String sourceCurrencyCode = getUserInput(scanner, "Enter source currency code: ", date.toString(), true);

            String targetCurrencyCode = getUserInput(scanner, "Enter target currency code (press ENTER if not applicable): ", date.toString(), false);

            double amount = getCustomAmount(scanner);

            ExchangeRateAPIHandler exchangeRateAPIHandler = new ExchangeRateAPIHandler(HttpClient.newHttpClient());
            CurrencyExchangeCalculateService currencyExchangeCalculateService = new CurrencyExchangeCalculateService(exchangeRateAPIHandler);

            try {
                CurrencyExchange result = currencyExchangeCalculateService.calculateExchangeAmount(sourceCurrencyCode, amount, targetCurrencyCode, date.toString());
                System.out.println("Result of currency exchange: " + result);
            } catch (DataNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (CurrencyNotFoundException e) {
                System.out.println("Invalid currency entered: " + e.getMessage());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date entered, correct format yyyy-MM-dd");
            }

        } catch (Exception e) {
            System.out.println("Another error: " + e.getMessage());
        }
    }

    private static LocalDate getDate(Scanner scanner) {
        LocalDate date = null;

        while (date == null) {
            System.out.print("Enter date (optional, press ENTER for today's date): ");
            String dateString = scanner.nextLine();

            try {
                date = dateString.isEmpty() ? LocalDate.now() : LocalDate.parse(dateString);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter a valid date in the format yyyy-MM-dd");
            }
        }
        return date;
    }

    public static boolean isCurrencyAvailable(String currency, String date) {
        List<String> codeCurrencyAvailableFor2011AndLater = Arrays.asList("USD", "AUD", "CAD", "EUR", "HUF", "CHF", "GBP", "JPY", "CZK", "DKK", "NOK", "SEK", "XDR");
        List<String> codeCurrencyAvailableFor2010AndBefore = Arrays.asList("USD", "AUD", "CAD", "EUR", "HUF", "CHF", "GBP", "JPY", "CZK", "DKK", "EEK", "NOK", "SEK", "XDR");

        int year = Integer.parseInt(date.substring(0, 4));

        List<String> availableCurrencies;

        if (year >= 2011) {
            availableCurrencies = codeCurrencyAvailableFor2011AndLater;
        } else if (year >= 2003) {
            availableCurrencies = codeCurrencyAvailableFor2010AndBefore;
        } else {
            availableCurrencies = new ArrayList<>();
        }

        return availableCurrencies.stream()
                .map(String::toLowerCase)
                .toList()
                .contains(currency.toLowerCase());
    }

    private static String getUserInput(Scanner scanner, String message, String date, boolean isSourceCurrency) {
        String currencyCode;
        do {
            System.out.print(message);
            currencyCode = scanner.nextLine();
        } while ((!currencyCode.isEmpty() && !isCurrencyAvailable(currencyCode, date)) || (isSourceCurrency && currencyCode.isEmpty()));
        return currencyCode;
    }

    private static double getCustomAmount(Scanner scanner) {
        double amount = 1;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Enter custom amount (or press ENTER for default 1): ");
            String userInput = scanner.nextLine().trim();

            if (!userInput.isEmpty()) {
                try {
                    amount = Double.parseDouble(userInput);
                    validInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            } else {
                validInput = true;
            }
        }

        System.out.println("Selected amount: " + amount);
        return amount;
    }
}