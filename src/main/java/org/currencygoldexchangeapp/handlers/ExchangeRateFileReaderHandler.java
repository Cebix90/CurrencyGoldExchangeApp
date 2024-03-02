package org.currencygoldexchangeapp.handlers;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.exceptions.CurrencyNotFoundException;
import org.currencygoldexchangeapp.exceptions.DataNotFoundException;
import org.currencygoldexchangeapp.services.CurrencyExchangeCalculateService;
import org.currencygoldexchangeapp.utils.InputUtility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ExchangeRateFileReaderHandler {
    private final CurrencyExchangeCalculateService currencyExchangeCalculateService;
    private final Map<String, String> errorMessages;
    private final Map<String, Double> exchangeRatesFromFile;
    private String errorReason;

    public ExchangeRateFileReaderHandler(CurrencyExchangeCalculateService currencyExchangeCalculateService) {
        this.currencyExchangeCalculateService = currencyExchangeCalculateService;
        this.errorMessages = new HashMap<>();
        this.exchangeRatesFromFile = new HashMap<>();
    }

    public Map<String, Double> readExchangeRates(String fileToReadPath) throws IOException {
        Path filePath = Paths.get(fileToReadPath);

        if (!java.nio.file.Files.exists(filePath)) {
            throw new IOException("NonExistingFileError: The file does not exist: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            if (!reader.ready()) {
                throw new IOException("EmptyFileError: The file is empty: " + filePath);
            }

            if (!filePath.toString().toLowerCase().endsWith(".csv")) {
                throw new IOException("InvalidFileFormatError: Invalid file format. Expected CSV file: " + filePath);
            }

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                boolean isLineProcessed = processExchangeRateLine(line);
                if (!isLineProcessed) {
                    recordError(errorReason, lineNumber, line);
                }
            }

            return exchangeRatesFromFile;
        }
    }

    public List<String> getErrorMessages() {
        return new ArrayList<>(errorMessages.values());
    }

    private boolean processExchangeRateLine(String line) {
        String[] parts = line.split(" ");

        if (parts.length == 4) {
            String sourceCurrency = parts[0].trim();
            String amountAsString = parts[1].trim();
            String targetCurrency = parts[2].trim();
            String dateString = parts[3];

            Optional<Double> parsedAmountOptional = parseAmount(amountAsString);
            double amount;
            if (parsedAmountOptional.isPresent() && parsedAmountOptional.get() > 0) {
                amount = parsedAmountOptional.get();
            } else {
                errorReason = "Invalid amount";
                return false;
            }

            if (!isValidDateFormat(dateString)) {
                errorReason = "Invalid date format";
                return false;
            }

            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yy"));
            String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            Optional<String> validatedSourceCurrencyOptional = validateSourceCurrency(sourceCurrency, formattedDate);
            if(validatedSourceCurrencyOptional.isPresent()){
                sourceCurrency = validatedSourceCurrencyOptional.get();
            } else {
                errorReason = "Invalid source currency code";
                return false;
            }

            Optional<String> validatedTargetCurrencyOptional = validateTargetCurrency(targetCurrency, formattedDate);
            if (validatedTargetCurrencyOptional.isPresent()) {
                targetCurrency = validatedTargetCurrencyOptional.get();
            } else {
                errorReason = "Invalid target currency code";
                return false;
            }

            double exchangeRate = calculateExchangeRate(sourceCurrency, amount, targetCurrency, formattedDate);
            // "_" + line  <- added, I have to check why its showing me result in different way
            String uniqueKey = sourceCurrency + "_" + parts[1] + "_" + targetCurrency + "_" + dateString;

            exchangeRatesFromFile.put(uniqueKey, exchangeRate);
            return true;
        } else {
            errorReason = "Invalid line format";
            return false;
        }
    }

    private Optional<Double> parseAmount(String amountString) {
        try {
            return Optional.of(Double.parseDouble(amountString.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean isValidDateFormat(String dateString) {
        try {
            LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yy"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private Optional<String> validateSourceCurrency(String sourceCurrency, String formattedDate) {
        if (InputUtility.isCurrencyAvailable(sourceCurrency, formattedDate)) {
            return Optional.of(sourceCurrency);
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> validateTargetCurrency(String targetCurrency, String formattedDate) {
        if (InputUtility.isCurrencyAvailable(targetCurrency, formattedDate) || targetCurrency.equalsIgnoreCase("pln")) {
            return Optional.of(targetCurrency);
        } else {
            return Optional.empty();
        }
    }

    private void recordError(String errorReason, int lineNumber, String line) {
        String errorKey = errorReason + " in the file at line " + lineNumber;
        String errorMessage = errorKey + ": " + line;
        errorMessages.put(errorKey, errorMessage);
    }

    private double calculateExchangeRate(String sourceCurrency, double amount, String targetCurrency, String date) {
        try {
            CurrencyExchange result = currencyExchangeCalculateService.calculateExchangeAmount(sourceCurrency, amount, targetCurrency, date);
            return result.getAsk();
        } catch (DataNotFoundException | CurrencyNotFoundException e) {
            errorReason = "ExchangeRateCalculationError (" + e.getMessage() + ")";
        }

        return 0.0;
    }
}
