import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.handlers.ExchangeRateFileReaderHandler;
import org.currencygoldexchangeapp.services.CurrencyExchangeCalculateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        Map<String, Double> exchangeRates = fileReaderHandler.readExchangeRates();

        // Assert
        assertNotNull(exchangeRates);
        assertFalse(exchangeRates.isEmpty());
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
        Map<String, Double> exchangeRates = fileReaderHandler.readExchangeRates();
        List<String> errorMessages = fileReaderHandler.getErrorMessages();

        // Assert
        assertNotNull(exchangeRates);
        assertFalse(errorMessages.isEmpty());
        assertTrue(errorMessages.contains("Invalid source currency code in the file at line 8: SSS 100 CHF 12-02-24"));
        assertTrue(errorMessages.contains("Invalid target currency code in the file at line 4: USD 100 KKK 09-02-24"));
        assertTrue(errorMessages.contains("Invalid date format in the file at line 6: USD 100 JPY 109-02-24"));
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
        Map<String, Double> exchangeRates = fileReaderHandler.readExchangeRates();
        List<String> errorMessages = fileReaderHandler.getErrorMessages();

        // Assert
        assertNotNull(exchangeRates);
        assertFalse(errorMessages.isEmpty());
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
        Map<String, Double> exchangeRates = fileReaderHandler.readExchangeRates();
        List<String> errorMessages = fileReaderHandler.getErrorMessages();

        // Assert
        assertNotNull(exchangeRates);
        assertFalse(errorMessages.isEmpty());
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
        Map<String, Double> exchangeRates = fileReaderHandler.readExchangeRates();
        List<String> errorMessages = fileReaderHandler.getErrorMessages();

        // Assert
        assertNotNull(exchangeRates);
        assertFalse(errorMessages.isEmpty());
        assertTrue(errorMessages.contains("Invalid amount in the file at line 9: USD SSS CHF 12-02-24"));
    }

    @Test
    public void readExchangeRates_withEmptyFile() {
        // Arrange
        String pathToEmptyFile = "src/test/resources/emptyFile.csv";
        ExchangeRateFileReaderHandler fileReaderHandler = new ExchangeRateFileReaderHandler(pathToEmptyFile, mockCalculateService);

        // Act
        Map<String, Double> exchangeRates = fileReaderHandler.readExchangeRates();
        List<String> errorMessages = fileReaderHandler.getErrorMessages();

        // Assert
        assertTrue(exchangeRates.isEmpty(), "Exchange rates should be empty for an empty file.");
        assertFalse(errorMessages.isEmpty(), "Error messages should not be empty.");
        assertEquals(1, errorMessages.size(), "Exactly one error message should be generated for an empty file.");

        String expectedErrorMessage = "The file is empty: " + pathToEmptyFile;
        assertTrue(errorMessages.contains(expectedErrorMessage), "Error message should contain the expected message for an empty file.");
    }

    @Test
    public void readExchangeRates_withNonExistingFile() {
        // Arrange
        String nonExistingFilePath = "src/test/resources/nonExistingFile.csv";
        ExchangeRateFileReaderHandler fileReaderHandler = new ExchangeRateFileReaderHandler(nonExistingFilePath, mockCalculateService);

        // Act
        Map<String, Double> exchangeRates = fileReaderHandler.readExchangeRates();
        List<String> errorMessages = fileReaderHandler.getErrorMessages();

        // Assert
        assertTrue(exchangeRates.isEmpty());
        assertFalse(errorMessages.isEmpty());
        assertTrue(errorMessages.contains("Error reading exchange rates from the CSV file: src/test/resources/nonExistingFile.csv (The system cannot find the file specified)".replace("/", "\\")));

    }

    @Test
    public void readExchangeRates_withInvalidFileFormat() {
        // Arrange
        String pathToFileWithInvalidFormat = "src/test/resources/fileWithInvalidFormat.txt";
        ExchangeRateFileReaderHandler fileReaderHandler = new ExchangeRateFileReaderHandler(pathToFileWithInvalidFormat, mockCalculateService);

        // Act
        Map<String, Double> exchangeRates = fileReaderHandler.readExchangeRates();
        List<String> errorMessages = fileReaderHandler.getErrorMessages();

        // Assert
        assertTrue(exchangeRates.isEmpty());
        assertFalse(errorMessages.isEmpty());
        assertTrue(errorMessages.contains("Invalid file format. Expected CSV file: src/test/resources/fileWithInvalidFormat.txt"));
    }

    private static CurrencyExchange createCurrencyExchange() {
        CurrencyExchange currencyExchange = new CurrencyExchange();
        currencyExchange.setCode("USD");
        currencyExchange.setAsk(423);
        currencyExchange.setBid(423);
        return currencyExchange;
    }
}
