package org.currencygoldexchangeapp.handlers;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.exceptions.CurrencyNotFoundException;
import org.currencygoldexchangeapp.exceptions.DataNotFoundException;
import org.currencygoldexchangeapp.services.CurrencyExchangeCalculateService;
import org.currencygoldexchangeapp.utils.InputValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRateFileReaderHandler {
    private final String filePath;
    private final CurrencyExchangeCalculateService currencyExchangeCalculateService;
    private final Map<String, String> errorMessages;
    private final Map<String, Double> exchangeRatesFromFile;

    public ExchangeRateFileReaderHandler(String filePath, CurrencyExchangeCalculateService currencyExchangeCalculateService) {
        this.filePath = filePath;
        this.currencyExchangeCalculateService = currencyExchangeCalculateService;
        this.errorMessages = new HashMap<>();
        this.exchangeRatesFromFile = new HashMap<>();
    }

    public Map<String, Double> readExchangeRates() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            if (!reader.ready()) {
                recordError("EmptyFileError", "The file is empty: " + filePath);
                return exchangeRatesFromFile;
            }

            if (!filePath.toLowerCase().endsWith(".csv")) {
                recordError("InvalidFileFormatError", "Invalid file format. Expected CSV file: " + filePath);
                return exchangeRatesFromFile;
            }

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                processExchangeRateLine(line, lineNumber);
            }

            return exchangeRatesFromFile;
        } catch (IOException e) {
            handleFileReadError(e);
        }

        return exchangeRatesFromFile;
    }

    public List<String> getErrorMessages() {
        return new ArrayList<>(errorMessages.values());
    }

    private void processExchangeRateLine(String line, int lineNumber) {
        String[] parts = line.split(" ");

        if (parts.length == 4) {
            String sourceCurrency = parts[0].trim();
            String amountAsString = parts[1].trim();
            String targetCurrency = parts[2].trim();
            String dateString = parts[3];

            double amount = parseAmount(amountAsString, line, lineNumber);
            if (amount < 0) {
                return;
            }

            LocalDate date = parseDate(dateString, line, lineNumber);
            if (date == null) {
                return;
            }
            String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            sourceCurrency = validateSourceCurrency(line, lineNumber, sourceCurrency, formattedDate);
            if (sourceCurrency == null) {
                return;
            }

            targetCurrency = validateTargetCurrency(line, lineNumber, targetCurrency, formattedDate);
            if (targetCurrency == null) {
                return;
            }

            double exchangeRate = calculateExchangeRate(sourceCurrency, amount, targetCurrency, formattedDate, line);
            String uniqueKey = sourceCurrency + "_" + parts[1] + "_" + targetCurrency + "_" + dateString;

            exchangeRatesFromFile.put(uniqueKey, exchangeRate);
        } else {
            recordError("Invalid line format in the file at line " + lineNumber, "Invalid line format in the file: " + line);
        }
    }

    private double parseAmount(String amountString, String line, int lineNumber) {
        try {
            return Double.parseDouble(amountString.trim());
        } catch (NumberFormatException e) {
            String errorKey = "Invalid amount in the file at line " + lineNumber;
            recordError(errorKey, "Invalid amount in the file at line " + lineNumber + ": " + line);
            return -1;
        }
    }


    private LocalDate parseDate(String dateString, String line, int lineNumber) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yy"));
        } catch (DateTimeParseException e) {
            recordError("Invalid date format in the file at line " + lineNumber, "Invalid date format in the file at line " + lineNumber + ": " + line);
            return null;
        }
    }

    private String validateSourceCurrency(String line, int lineNumber, String sourceCurrency, String formattedDate) {
        if (InputValidator.isCurrencyAvailable(sourceCurrency, formattedDate)) {
            return sourceCurrency;
        } else {
            String errorMessage = "Invalid source currency code in the file at line " + lineNumber + ": " + line;
            recordError("Invalid source currency code in the file at line " + lineNumber, errorMessage);
            return null;
        }
    }

    private String validateTargetCurrency(String line, int lineNumber, String targetCurrency, String formattedDate) {
        if (InputValidator.isCurrencyAvailable(targetCurrency, formattedDate) || targetCurrency.equalsIgnoreCase("pln")) {
            return targetCurrency;
        } else {
            String errorMessage = "Invalid target currency code in the file at line " + lineNumber + ": " + line;
            recordError("Invalid target currency code in the file at line " + lineNumber, errorMessage);
            return null;
        }
    }

    private void handleFileReadError(IOException e) {
        recordError("FileReadError", "Error reading exchange rates from the CSV file: " + e.getMessage());
    }

    private void recordError(String errorKey, String errorMessage) {
        errorMessages.put(errorKey, errorMessage);
    }

    private double calculateExchangeRate(String sourceCurrency, double amount, String targetCurrency, String date, String line) {
        try {
            CurrencyExchange result = currencyExchangeCalculateService.calculateExchangeAmount(sourceCurrency, amount, targetCurrency, date);
            return result.getAsk();
        } catch (DataNotFoundException | CurrencyNotFoundException e) {
            recordError("ExchangeRateCalculationError at line", e.getMessage() + ": " + line);
        }

        return 0.0;
    }
}
