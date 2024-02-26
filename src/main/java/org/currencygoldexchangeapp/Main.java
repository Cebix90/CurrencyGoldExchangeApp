package org.currencygoldexchangeapp;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.exceptions.CurrencyNotFoundException;
import org.currencygoldexchangeapp.exceptions.DataNotFoundException;
import org.currencygoldexchangeapp.handlers.ExchangeRateAPIHandler;
import org.currencygoldexchangeapp.handlers.ExchangeRateFileReaderHandler;
import org.currencygoldexchangeapp.services.CurrencyExchangeCalculateService;
import org.currencygoldexchangeapp.utils.InputUtility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose an option:");
        System.out.println("1. Enter data manually");
        System.out.println("2. Load data from a file");

        var input = scanner.nextLine();
        int option;
        try {
            option = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Conversion error: " + e.getMessage());
            option = -1;
        }

        ExchangeRateAPIHandler exchangeRateAPIHandler = new ExchangeRateAPIHandler(HttpClient.newHttpClient());
        CurrencyExchangeCalculateService currencyExchangeCalculateService = new CurrencyExchangeCalculateService(exchangeRateAPIHandler);

        if (option == 1) {
            LocalDate date = InputUtility.getDate(scanner);
            String sourceCurrencyCode = getUserInput(scanner, "Enter source currency code: ", date.toString(), true);
            String targetCurrencyCode = getUserInput(scanner, "Enter target currency code (press ENTER for PLN currency): ", date.toString(), false);
            if(targetCurrencyCode.isEmpty()) {
                targetCurrencyCode = "PLN";
            }
            double amount = InputUtility.getCustomAmount(scanner);

            currencyExchangeCalculateService = new CurrencyExchangeCalculateService(exchangeRateAPIHandler);

            try {
                CurrencyExchange result = currencyExchangeCalculateService.calculateExchangeAmount(sourceCurrencyCode, amount, targetCurrencyCode, date.toString());
                System.out.println("Result of currency exchange: " + sourceCurrencyCode + " " + amount + " " + targetCurrencyCode + " " + date + ": " + result);

                saveResultToCSV(sourceCurrencyCode, amount, targetCurrencyCode, date, result);
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
    }

    private static String getUserInput(Scanner scanner, String message, String date, boolean isSourceCurrency) {
        String currencyCode;
        do {
            System.out.print(message);
            currencyCode = scanner.nextLine().toUpperCase();

            if ("PLN".equalsIgnoreCase(currencyCode) && !isSourceCurrency) {
                return currencyCode;
            }
        } while ((!currencyCode.isEmpty() && !InputUtility.isCurrencyAvailable(currencyCode, date)) || (isSourceCurrency && currencyCode.isEmpty()));
        return currencyCode;
    }

    private static void displayResultsFromFile(String filePath, CurrencyExchangeCalculateService currencyExchangeCalculateService) {
        try {
            ExchangeRateFileReaderHandler fileHandler = new ExchangeRateFileReaderHandler(currencyExchangeCalculateService);
            Map<String, Double> exchangeRates = fileHandler.readExchangeRates(filePath);

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

    private static void saveResultToCSV(String sourceCurrencyCode, double amount, String targetCurrencyCode, LocalDate date, CurrencyExchange result) {
        String filePath = "resultQueryToAPI.csv";
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {

            if (!java.nio.file.Files.exists(Paths.get(filePath)) || java.nio.file.Files.size(Paths.get(filePath)) == 0) {
                writer.write("SourceCurrency Amount TargetCurrency Date Bid Ask\n");
            }

            String csvLine = String.format("%s %.2f %s %s %.4f %.4f\n", sourceCurrencyCode, amount, targetCurrencyCode, date, result.getBid(), result.getAsk());
            writer.write(csvLine);
        } catch (IOException e) {
            System.err.println("An error occurred while saving results to CSV file: " + e.getMessage());
        }
    }

}