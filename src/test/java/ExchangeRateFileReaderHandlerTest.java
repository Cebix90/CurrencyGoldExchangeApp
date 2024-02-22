import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.handlers.ExchangeRateFileReaderHandler;
import org.currencygoldexchangeapp.services.CurrencyExchangeCalculateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class ExchangeRateFileReaderHandlerTest {
    @Mock
    CurrencyExchangeCalculateService mockCalculateService;

    @Test
    public void readExchangeRates_withValidRecords() {
        // Arrange
        String pathToTheFile = "src/test/resources/test2.csv";
        mockCalculateService = mock(CurrencyExchangeCalculateService.class);

        ExchangeRateFileReaderHandler fileReaderHandler = new ExchangeRateFileReaderHandler(pathToTheFile, mockCalculateService);

        when(mockCalculateService.calculateExchangeAmount(anyString(), anyDouble(), anyString(), anyString()))
                .thenReturn(createCurrencyExchange());

        // Act
        Map<String, Double> exchangeRates;
        try {
            exchangeRates = fileReaderHandler.readExchangeRates();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Assert
        assertNotNull(exchangeRates, "Exchange rates list shouldn't be null if file contains correct records.");
        assertFalse(exchangeRates.isEmpty(), "Exchange rates list shouldn't be empty if file contains correct records.");
        assertTrue(exchangeRates.containsKey("USD_100_PLN_27-12-23"));
    }

    @Test
    public void readExchangeRates_withInvalidRecord_SourceCurrency() {
        // Arrange
        String pathToTheFile = "src/test/resources/test2.csv";
        mockCalculateService = mock(CurrencyExchangeCalculateService.class);

        ExchangeRateFileReaderHandler fileReaderHandler = new ExchangeRateFileReaderHandler(pathToTheFile, mockCalculateService);

        when(mockCalculateService.calculateExchangeAmount(anyString(), anyDouble(), anyString(), anyString()))
                .thenReturn(createCurrencyExchange());

        // Act
        Map<String, Double> exchangeRates;
        try {
            exchangeRates = fileReaderHandler.readExchangeRates();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> errorMessages = fileReaderHandler.getErrorMessages();

        // Assert
        assertNotNull(exchangeRates, "Exchange rates list shouldn't be null if file contains any records (including invalid records).");
        assertFalse(errorMessages.isEmpty(), "Error messages shouldn't be empty if file contains records with invalid source currency.");
        assertTrue(errorMessages.contains("Invalid source currency code in the file at line 8: SSS 100 CHF 12-02-24"));
    }

    @Test
    public void readExchangeRates_withInvalidRecord_TargetCurrency() {
        // Arrange
        String pathToTheFile = "src/test/resources/test2.csv";
        mockCalculateService = mock(CurrencyExchangeCalculateService.class);

        ExchangeRateFileReaderHandler fileReaderHandler = new ExchangeRateFileReaderHandler(pathToTheFile, mockCalculateService);

        when(mockCalculateService.calculateExchangeAmount(anyString(), anyDouble(), anyString(), anyString()))
                .thenReturn(createCurrencyExchange());

        // Act
        Map<String, Double> exchangeRates;
        try {
            exchangeRates = fileReaderHandler.readExchangeRates();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> errorMessages = fileReaderHandler.getErrorMessages();

        // Assert
        assertNotNull(exchangeRates, "Exchange rates list shouldn't be null if file contains any records (including invalid records).");
        assertFalse(errorMessages.isEmpty(), "Error messages shouldn't be empty if file contains records with invalid target currency.");
        assertTrue(errorMessages.contains("Invalid target currency code in the file at line 4: USD 100 KKK 09-02-24"));
    }

    @Test
    public void readExchangeRates_withInvalidRecord_Date() {
        // Arrange
        String pathToTheFile = "src/test/resources/test2.csv";
        mockCalculateService = mock(CurrencyExchangeCalculateService.class);

        ExchangeRateFileReaderHandler fileReaderHandler = new ExchangeRateFileReaderHandler(pathToTheFile, mockCalculateService);

        when(mockCalculateService.calculateExchangeAmount(anyString(), anyDouble(), anyString(), anyString()))
                .thenReturn(createCurrencyExchange());

        // Act
        Map<String, Double> exchangeRates;
        try {
            exchangeRates = fileReaderHandler.readExchangeRates();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> errorMessages = fileReaderHandler.getErrorMessages();

        // Assert
        assertNotNull(exchangeRates, "Exchange rates list shouldn't be null if file contains any records (including invalid records).");
        assertFalse(errorMessages.isEmpty(), "Error messages shouldn't be empty if file contains records with invalid date format.");
        assertTrue(errorMessages.contains("Invalid date format in the file at line 6: USD 100 JPY 109-02-24"));
    }

    @Test
    public void readExchangeRates_withInvalidRecord_Amount() {
        // Arrange
        String pathToTheFile = "src/test/resources/test2.csv";
        mockCalculateService = mock(CurrencyExchangeCalculateService.class);

        ExchangeRateFileReaderHandler fileReaderHandler = new ExchangeRateFileReaderHandler(pathToTheFile, mockCalculateService);

        when(mockCalculateService.calculateExchangeAmount(anyString(), anyDouble(), anyString(), anyString()))
                .thenReturn(createCurrencyExchange());

        // Act
        Map<String, Double> exchangeRates;
        try {
            exchangeRates = fileReaderHandler.readExchangeRates();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> errorMessages = fileReaderHandler.getErrorMessages();

        // Assert
        assertNotNull(exchangeRates, "Exchange rates list shouldn't be null if file contains any records (including invalid records).");
        assertFalse(errorMessages.isEmpty(), "Error messages shouldn't be empty if file contains records with invalid amount.");
        assertTrue(errorMessages.contains("Invalid amount in the file at line 9: USD SSS CHF 12-02-24"));
    }

    @Test
    public void readExchangeRates_withEmptyFile() {
        // Arrange
        String pathToEmptyFile = "src/test/resources/emptyFile.csv";
        ExchangeRateFileReaderHandler fileReaderHandler = new ExchangeRateFileReaderHandler(pathToEmptyFile, mockCalculateService);

        // Act
        IOException exception = assertThrows(IOException.class, fileReaderHandler::readExchangeRates);

        // Assert
        assertEquals(("EmptyFileError: The file is empty: " + pathToEmptyFile).replace("/", "\\"), exception.getMessage(),
                "Exception message should match the expected message for an empty file.");

        List<String> errorMessages = fileReaderHandler.getErrorMessages();
        assertTrue(errorMessages.isEmpty(), "Error messages should be empty for an empty file.");
    }

    @Test
    public void readExchangeRates_withNonExistingFile() {
        // Arrange
        String nonExistingFilePath = "src/test/resources/nonExistingFile.csv";
        ExchangeRateFileReaderHandler fileReaderHandler = new ExchangeRateFileReaderHandler(nonExistingFilePath, mockCalculateService);

        // Act
        IOException exception = assertThrows(IOException.class, fileReaderHandler::readExchangeRates);

        // Assert
        assertEquals(("NonExistingFileError: The file does not exist: " + nonExistingFilePath).replace("/", "\\"), exception.getMessage(),
                "Exception message should match the expected message for an empty file.");

        List<String> errorMessages = fileReaderHandler.getErrorMessages();
        assertTrue(errorMessages.isEmpty(), "Error messages should be empty for an empty file.");
    }

    @Test
    public void readExchangeRates_withInvalidFileFormat() {
        // Arrange
        String pathToFileWithInvalidFormat = "src/test/resources/fileWithInvalidFormat.txt";
        ExchangeRateFileReaderHandler fileReaderHandler = new ExchangeRateFileReaderHandler(pathToFileWithInvalidFormat, mockCalculateService);

        // Act
        IOException exception = assertThrows(IOException.class, fileReaderHandler::readExchangeRates);

        // Assert
        assertEquals(("InvalidFileFormatError: Invalid file format. Expected CSV file: " + pathToFileWithInvalidFormat).replace("/", "\\"), exception.getMessage(),
                "Exception message should match the expected message for an invalid file format.");

        List<String> errorMessages = fileReaderHandler.getErrorMessages();
        assertTrue(errorMessages.isEmpty(), "Error messages should be empty for an invalid file format.");
    }

    private static CurrencyExchange createCurrencyExchange() {
        CurrencyExchange currencyExchange = new CurrencyExchange();
        currencyExchange.setCode("USD");
        currencyExchange.setAsk(423);
        currencyExchange.setBid(423);
        return currencyExchange;
    }
}
