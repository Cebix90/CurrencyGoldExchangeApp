package org.currencygoldexchangeapp;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.exceptions.CurrencyNotFoundException;
import org.currencygoldexchangeapp.exceptions.DataNotFoundException;
import org.currencygoldexchangeapp.handlers.ExchangeRateAPIHandler;
import org.currencygoldexchangeapp.services.CurrencyExchangeCalculateService;

import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter source currency code: ");
            String sourceCurrencyCode = scanner.next();

            System.out.print("Enter target currency code (press ENTER if not applicable): ");
            scanner.nextLine();
            String targetCurrencyCode = scanner.nextLine();

            double amount = getCustomAmount(scanner);

            System.out.print("Enter date (optional, press ENTER for today's date): ");
            String dateString = scanner.nextLine();

            ExchangeRateAPIHandler exchangeRateAPIHandler = new ExchangeRateAPIHandler(HttpClient.newHttpClient());
            CurrencyExchangeCalculateService currencyExchangeCalculateService = new CurrencyExchangeCalculateService(exchangeRateAPIHandler);

            try {
                LocalDate date = dateString.isEmpty() ? LocalDate.now() : LocalDate.parse(dateString);
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

    private static double getCustomAmount(Scanner scanner) {
        System.out.print("Enter custom amount (or press ENTER for default 1): ");
        double amount = 1;

        int maxAttempts = 3;
        int attempts = 0;

        while (attempts < maxAttempts) {
            String userInput = scanner.nextLine().trim();
            if (!userInput.isEmpty()) {
                try {
                    amount = Double.parseDouble(userInput);
                    break;
                } catch (NumberFormatException e) {
                    attempts++;
                    System.out.println("Invalid input. Please enter a valid number or press ENTER for default 1. " + (maxAttempts - attempts) + " attempts left");
                }
            } else {
                break;
            }
        }

        if (attempts == maxAttempts) {
            System.out.println("Exceeded maximum attempts. Using default value of 1.");
        }

        System.out.println("Selected amount: " + amount);
        return amount;
    }
}