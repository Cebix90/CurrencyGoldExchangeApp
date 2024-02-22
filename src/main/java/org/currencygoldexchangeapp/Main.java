package org.currencygoldexchangeapp;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.exceptions.CurrencyNotFoundException;
import org.currencygoldexchangeapp.exceptions.DataNotFoundException;
import org.currencygoldexchangeapp.handlers.ExchangeRateAPIHandler;
import org.currencygoldexchangeapp.handlers.ExchangeRateFileReaderHandler;
import org.currencygoldexchangeapp.services.CurrencyExchangeCalculateService;
import org.currencygoldexchangeapp.utils.InputUtility;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Choose an option:");
            System.out.println("1. Enter data manually");
            System.out.println("2. Load data from a file");

            int option = scanner.nextInt();
            scanner.nextLine();

            ExchangeRateAPIHandler exchangeRateAPIHandler = new ExchangeRateAPIHandler(HttpClient.newHttpClient());
            CurrencyExchangeCalculateService currencyExchangeCalculateService = new CurrencyExchangeCalculateService(exchangeRateAPIHandler);

            if (option == 1) {
                LocalDate date = InputUtility.getDate(scanner);
                String sourceCurrencyCode = getUserInput(scanner, "Enter source currency code: ", date.toString(), true);
                String targetCurrencyCode = getUserInput(scanner, "Enter target currency code (press ENTER if not applicable): ", date.toString(), false);
                double amount = InputUtility.getCustomAmount(scanner);

                exchangeRateAPIHandler = new ExchangeRateAPIHandler(HttpClient.newHttpClient());
                currencyExchangeCalculateService = new CurrencyExchangeCalculateService(exchangeRateAPIHandler);

                try {
                    CurrencyExchange result = currencyExchangeCalculateService.calculateExchangeAmount(sourceCurrencyCode, amount, targetCurrencyCode, date.toString());
                    System.out.println("Result of currency exchange: " + result);
                } catch (DataNotFoundException e) {
                    System.out.println(e.getMessage());
                } catch (CurrencyNotFoundException e) {
                    System.out.println("Invalid currency code entered: " + e.getMessage());
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date entered. Correct format is yyyy-MM-dd");
                }
            } else if (option == 2) {
                System.out.print("Enter the file path: ");
                String filePath = scanner.nextLine();

                displayResultsFromFile(filePath, currencyExchangeCalculateService);
            } else {
                System.out.println("Invalid option. Closing the program...");
            }
        } catch (Exception e) { // This will be solved, I know that general Exception is not a good idea
            System.out.println("Another error: " + e.getMessage());
        }
    }

    private static String getUserInput(Scanner scanner, String message, String date, boolean isSourceCurrency) {
        String currencyCode;
        do {
            System.out.print(message);
            currencyCode = scanner.nextLine();

            if ("PLN".equalsIgnoreCase(currencyCode) && !isSourceCurrency) {
                return currencyCode;
            }
        } while ((!currencyCode.isEmpty() && !InputUtility.isCurrencyAvailable(currencyCode, date)) || (isSourceCurrency && currencyCode.isEmpty()));
        return currencyCode;
    }

    private static void displayResultsFromFile(String filePath, CurrencyExchangeCalculateService currencyExchangeCalculateService) {
        try {
            ExchangeRateFileReaderHandler fileHandler = new ExchangeRateFileReaderHandler(filePath, currencyExchangeCalculateService);
            Map<String, Double> exchangeRates = fileHandler.readExchangeRates();

            if (!exchangeRates.isEmpty()) {
                System.out.println("Results based on data from the file:");
                for (Map.Entry<String, Double> entry : exchangeRates.entrySet()) {
                    if (entry.getValue() != 0.0) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
                }
            }

            if (!fileHandler.getErrorMessages().isEmpty()) {
                System.out.println("Errors encountered while processing the file:");
                for (String errorMessage : fileHandler.getErrorMessages()) {
                    System.out.println(errorMessage);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading exchange rates from the CSV file: " + e.getMessage());
        }
    }
}