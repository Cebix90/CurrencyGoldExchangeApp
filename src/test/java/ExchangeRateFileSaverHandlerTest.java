import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.handlers.ExchangeRateFileSaverHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRateFileSaverHandlerTest {
    private ExchangeRateFileSaverHandler fileSaverHandler;

    @TempDir
    Path tempDir;

    @Test
    public void saveExchangeRates_testSaveResultToCSV() {
        // Arrange
        String sourceCurrencyCode = "USD";
        double amount = 1.00;
        String targetCurrencyCode = "PLN";
        LocalDate date = LocalDate.of(2024, 2, 26);
        CurrencyExchange result = new CurrencyExchange();
        result.setBid(3.9448);
        result.setAsk(4.0244);

        fileSaverHandler = new ExchangeRateFileSaverHandler();

        // Act & Assert
        boolean isSaved = fileSaverHandler.saveResultToCSV(tempDir, sourceCurrencyCode, amount, targetCurrencyCode, date, result);

        assertTrue(isSaved, "The result should be saved successfully.");

        try (BufferedReader reader = new BufferedReader(new FileReader(tempDir.resolve("/resultQueryToAPI_20240226.csv").toFile()))) {
            String headerLine = reader.readLine();
            assertEquals("SourceCurrency,Amount,TargetCurrency,Date,Bid,Ask", headerLine, "The header line is incorrect.");

            String dataLine = reader.readLine();
            String expectedDataLine = String.format("%s,%.2f,%s,%s,%.4f,%.4f", sourceCurrencyCode, amount, targetCurrencyCode, date, result.getBid(), result.getAsk());
            assertEquals(expectedDataLine, dataLine, "The data line is incorrect.");
        } catch (IOException e) {
            System.err.println("An error occurred while reading the CSV file: " + e.getMessage());
        }
    }

    @Test
    public void saveExchangeRates_testFileName() {
        // Arrange
        String sourceCurrencyCode = "USD";
        double amount = 1.00;
        String targetCurrencyCode = "PLN";
        LocalDate date = LocalDate.of(2024, 2, 26);
        CurrencyExchange result = new CurrencyExchange();
        result.setBid(3.9448);
        result.setAsk(4.0244);

        fileSaverHandler = new ExchangeRateFileSaverHandler();

        String expectedFileName = "resultQueryToAPI_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
        Path expectedFilePath = tempDir.resolve(expectedFileName);

        // Act
        boolean isSaved = fileSaverHandler.saveResultToCSV(tempDir, sourceCurrencyCode, amount, targetCurrencyCode, date, result);

        // Assert
        assertTrue(isSaved, "The result should be saved successfully");
        assertTrue(Files.exists(expectedFilePath), "The file should exist with the correct name.");
    }

    @Test
    public void saveExchangeRates_NoDuplicates() {
        // Arrange
        String sourceCurrencyCode = "USD";
        double amount = 1.00;
        String targetCurrencyCode = "PLN";
        LocalDate date = LocalDate.parse("2024-02-26");
        CurrencyExchange result = new CurrencyExchange();
        result.setBid(3.9448);
        result.setAsk(4.0244);

        fileSaverHandler = new ExchangeRateFileSaverHandler();

        String expectedFileName = "resultQueryToAPI_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
        Path expectedFilePath = tempDir.resolve(expectedFileName);

        // Act
        boolean isSaved = fileSaverHandler.saveResultToCSV(tempDir, sourceCurrencyCode, amount, targetCurrencyCode, date, result);

        // Assert
        assertTrue(isSaved, "Result should be saved successfully.");

        try (Stream<String> lines = java.nio.file.Files.lines(expectedFilePath)) {
            long lineCount = lines.count();
            assertEquals(2, lineCount, "File should contain only two lines.");
        } catch (Exception e) {
            fail("Error occurred while reading lines from the file: " + e.getMessage());
        }
    }

    @Test
    public void saveExchangeRates_DuplicatesIgnored() {
        // Arrange
        String sourceCurrencyCode = "USD";
        double amount = 1.00;
        String targetCurrencyCode = "PLN";
        LocalDate date = LocalDate.parse("2024-02-26");
        CurrencyExchange result = new CurrencyExchange();
        result.setBid(3.9448);
        result.setAsk(4.0244);

        fileSaverHandler = new ExchangeRateFileSaverHandler();

        String expectedFileName = "resultQueryToAPI_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
        Path expectedFilePath = tempDir.resolve(expectedFileName);

        // Act & Assert
        boolean isSavedFirstTime = fileSaverHandler.saveResultToCSV(tempDir, sourceCurrencyCode, amount, targetCurrencyCode, date, result);
        assertTrue(isSavedFirstTime, "The result should be saved successfully the first time.");

        boolean isSavedSecondTime = fileSaverHandler.saveResultToCSV(tempDir, sourceCurrencyCode, amount, targetCurrencyCode, date, result);
        assertFalse(isSavedSecondTime, "The result should not be saved the second time if it's the same.");

        try (Stream<String> lines = Files.lines(expectedFilePath)) {
            long lineCount = lines.count();
            assertEquals(2, lineCount, "There should be only two lines in the file (header and one data line).");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
