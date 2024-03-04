package org.currencygoldexchangeapp.handlers;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class ExchangeRateFileSaverHandler {
    public boolean saveResultToCSV(Path inputFilePath, String sourceCurrencyCode, double amount, String targetCurrencyCode, LocalDate date, CurrencyExchange result) {
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String filePath = inputFilePath + "/resultQueryToAPI_" + formattedDate + ".csv";
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {

            var getFilePath = Paths.get(filePath);

            String csvLine = String.format("%s,%.2f,%s,%s,%.4f,%.4f\n", sourceCurrencyCode, amount, targetCurrencyCode, date, result.getBid(), result.getAsk());

            if (!java.nio.file.Files.exists(getFilePath) || java.nio.file.Files.size(getFilePath) == 0) {
                writer.write("SourceCurrency,Amount,TargetCurrency,Date,Bid,Ask\n");
                writer.write(csvLine);
                return true;
            } else {
                try (Stream<String> lines = Files.lines(getFilePath)) {
                    boolean hasSameLine = lines.anyMatch(line -> line.trim().equalsIgnoreCase(csvLine.trim()));
                    if (!hasSameLine) {
                        writer.write(csvLine);
                        return true;
                    }
                }
            }

            return false;
        } catch (IOException e) {
            System.err.println("An error occurred while saving results to CSV file: " + e.getMessage());
            return false;
        }
    }
}
