package org.currencygoldexchangeapp.services;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.datamodels.GoldValue;
import org.currencygoldexchangeapp.exceptions.CurrencyNotFoundException;
import org.currencygoldexchangeapp.exceptions.DataNotFoundException;
import org.currencygoldexchangeapp.handlers.ExchangeRateAPIHandler;
import org.currencygoldexchangeapp.handlers.ExchangeRateFileReaderHandler;
import org.currencygoldexchangeapp.handlers.ExchangeRateFileSaverHandler;
import org.currencygoldexchangeapp.handlers.GoldValueAPIHandler;
import org.currencygoldexchangeapp.utils.InputUtility;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class UserMenuService {
    private final Scanner scanner = new Scanner(System.in);
    private final ExchangeRateFileReaderHandler exchangeRateFileReaderHandler;
    private final ExchangeRateFileSaverHandler exchangeRateFileSaverHandler;
    private final GoldValueAPIHandler goldValueAPIHandler;
    private final CurrencyExchangeCalculateService currencyExchangeCalculateService;
    private final GoldValueCalculateService goldValueCalculateService;

    public UserMenuService() {
        ExchangeRateAPIHandler exchangeRateAPIHandler = new ExchangeRateAPIHandler(HttpClient.newHttpClient());
        this.currencyExchangeCalculateService = new CurrencyExchangeCalculateService(exchangeRateAPIHandler);
        this.exchangeRateFileReaderHandler = new ExchangeRateFileReaderHandler(currencyExchangeCalculateService);
        this.exchangeRateFileSaverHandler = new ExchangeRateFileSaverHandler();
        this.goldValueAPIHandler = new GoldValueAPIHandler(HttpClient.newHttpClient());
        this.goldValueCalculateService = new GoldValueCalculateService(goldValueAPIHandler);
    }

    public void runApplication() {
        int choice = 0;
        while (choice != 5) {
            System.out.println();
            displayMenu();
            choice = getUserChoice();
            System.out.println();
            handleUserChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println("1. Currency exchange prices for a specific date.");
        System.out.println("2. Load data from a file and calculate currency exchange prices.");
        System.out.println("3. Gold price for a specific date.");
        System.out.println("4. Compare today's gold price with the best price from the last month/year.");
        System.out.println("5. Close the application");
    }

    private int getUserChoice() {
        System.out.print("Please enter your choice: ");
        String input = scanner.nextLine();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Conversion error: " + e.getMessage());
            return  -1;
        }
    }

    private void handleUserChoice(int choice) {
        switch (choice) {
            case 1:
                displaySingleCurrencyExchange(currencyExchangeCalculateService, exchangeRateFileSaverHandler);
                break;
            case 2:
                displayResultsFromFile(exchangeRateFileReaderHandler);
                break;
            case 3:
                displaySingleGoldValue(goldValueAPIHandler);
                break;
            case 4:
                displayComparisonForGoldValue(goldValueCalculateService);
                break;
            case 5:
                System.out.println("Have a nice day. Closing the program...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void displaySingleCurrencyExchange(CurrencyExchangeCalculateService currencyExchangeCalculateService, ExchangeRateFileSaverHandler exchangeRateFileSaverHandler) {
        LocalDate date = InputUtility.getDateForCurrencies(scanner);
        String sourceCurrencyCode = InputUtility.getUserInputForCurrency(scanner, "Enter source currency code: ", date.toString(), true);
        String targetCurrencyCode = InputUtility.getUserInputForCurrency(scanner, "Enter target currency code (press ENTER for PLN currency): ", date.toString(), false);
        if(targetCurrencyCode.isEmpty()) {
            targetCurrencyCode = "PLN";
        }
        double amount = InputUtility.getCustomAmount(scanner);

        try {
            CurrencyExchange result = currencyExchangeCalculateService.calculateExchangeAmount(sourceCurrencyCode, amount, targetCurrencyCode, date.toString());
            System.out.println("Result of currency exchange: " + sourceCurrencyCode + " " + amount + " " + targetCurrencyCode + " " + date + ": " + result);

            boolean resultToCSV = exchangeRateFileSaverHandler.saveResultToCSV(Path.of("savedFiles"), sourceCurrencyCode, amount, targetCurrencyCode, date, result);
            if (resultToCSV) {
                System.out.println("Results saved to CSV file successfully.");
            }

        } catch (DataNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (CurrencyNotFoundException e) {
            System.out.println("Invalid currency code entered: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date entered. Correct format is yyyy-MM-dd");
        }
    }

    private void displayResultsFromFile(ExchangeRateFileReaderHandler exchangeRateFileReaderHandler) {
        try {
            System.out.print("Enter the file path: ");
            String filePath = scanner.nextLine();

            Map<String, Double> exchangeRates = exchangeRateFileReaderHandler.readExchangeRates(filePath);

            if (!exchangeRates.isEmpty()) {
                System.out.println("Results based on data from the file:");
                for (Map.Entry<String, Double> entry : exchangeRates.entrySet()) {
                    if (entry.getValue() != 0.0) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
                }
            }

            if (!exchangeRateFileReaderHandler.getErrorMessages().isEmpty()) {
                System.out.println("Errors encountered while processing the file:");
                for (String errorMessage : exchangeRateFileReaderHandler.getErrorMessages()) {
                    System.out.println(errorMessage);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading exchange rates from the CSV file: " + e.getMessage());
        }
    }

    private void displaySingleGoldValue(GoldValueAPIHandler goldValueAPIHandler) {
        LocalDate goldDate = InputUtility.getDateForGold(scanner);

        GoldValue goldValue = goldValueAPIHandler.getGoldValueForSpecificDate(goldDate.toString());

        System.out.println("Value of gold on " + goldDate + ": " + goldValue.getValue() + " Pln/Gram");
    }

    private void displayComparisonForGoldValue(GoldValueCalculateService goldValueCalculateService) {

        String endDate = LocalDate.now().toString();
        String startDateMonthly = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1).toString();
        String startDateYearly = LocalDate.now().minusYears(1).toString();

        Optional<BigDecimal> gainOrLossMonthly = goldValueCalculateService.calculateGainOrLoss(startDateMonthly, endDate);
        Optional<BigDecimal> gainOrLossYearly = goldValueCalculateService.calculateGainOrLoss(startDateYearly, endDate);

        if(gainOrLossMonthly.isPresent()) {
            System.out.println("Today's gold price compared to the best price this month: " + gainOrLossMonthly.get() + " PLN");
        } else {
            System.out.println("No results for this date range. No comparison for monthly results.");
        }

        if(gainOrLossYearly.isPresent()) {
            System.out.println("Today's gold price compared to the best price this year: " + gainOrLossYearly.get() + " PLN");
        } else {
            System.out.println("No results for this date range. No comparison for yearly results.");
        }
    }
}
