import org.apache.commons.io.IOUtils;
import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.datamodels.CurrencyRate;
import org.currencygoldexchangeapp.handlers.ExchangeRateAPIHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateAPIHandlerTest {

    @Mock
    private HttpClient client;

    @Mock
    private HttpResponse<String> response;

    @InjectMocks
    private ExchangeRateAPIHandler handler;

    @Test
    public void testGetExchangeRateSingleCurrency_ReturnsCorrectCurrencyExchange() {
        // Arrange
        String currency = "USD";
        String date = "2024-01-16";
        String expectedResponse = loadJsonFromFile("sample.json");

        CurrencyExchange expectedCurrencyExchange = new CurrencyExchange();
        expectedCurrencyExchange.setCode(currency);
        CurrencyRate rate = new CurrencyRate();
        rate.setBid(3.9570);
        rate.setAsk(4.0370);
        expectedCurrencyExchange.setRates(Collections.singletonList(rate));

        when(response.body()).thenReturn(expectedResponse);
        when(response.statusCode()).thenReturn(200);
        try {
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                    .thenReturn(response);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }


        // Act
        CurrencyExchange actualCurrencyExchange = handler.getExchangeRateSingleCurrency(currency, date);

        // Assert
        assertEquals(expectedCurrencyExchange.getCode(), actualCurrencyExchange.getCode());
        assertEquals(expectedCurrencyExchange.getRates().getFirst().getBid(), actualCurrencyExchange.getRates().getFirst().getBid());
        assertEquals(expectedCurrencyExchange.getRates().getFirst().getAsk(), actualCurrencyExchange.getRates().getFirst().getAsk());
        try {
            verify(client, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetExchangeRateSingleCurrency_ThrowsException() throws Exception {
        // Arrange
        String currency = "USD";
        String date = "2024-01-16";

        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenThrow(new IOException("Failed to send request"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> handler.getExchangeRateSingleCurrency(currency, date));
    }

    @Test
    public void testGetExchangeRateSingleCurrency_ThrowsRuntimeException_WhenHttpResponseIsNotOk() throws Exception {
        // Arrange
        String currency = "USD";
        String date = "2024-01-16";

        when(response.statusCode()).thenReturn(201);
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> handler.getExchangeRateSingleCurrency(currency, date));
    }

    @Test
    public void testGetExchangeRateSingleCurrency_ThrowsRuntimeException_WhenIOExceptionOccurs() throws Exception {
        // Arrange
        String currency = "USD";
        String date = "2024-01-16";

        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenThrow(IOException.class);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> handler.getExchangeRateSingleCurrency(currency, date));
    }

    @Test
    public void testGetExchangeRateSingleCurrency_WhenCurrencyIsEmpty() {
        // Arrange
        String currency = "";
        String date = "2024-01-16";

        // Act and Assert
        assertThrows(Exception.class, () -> handler.getExchangeRateSingleCurrency(currency, date));
    }

    @Test
    public void testGetExchangeRateSingleCurrency_WhenDateIsEmpty() {
        // Arrange
        String currency = "USD";
        String date = "";

        // Act and Assert
        assertThrows(Exception.class, () -> handler.getExchangeRateSingleCurrency(currency, date));
    }

    @Test
    public void testGetExchangeRateSingleCurrency_WhenDateIsNull() {
        // Arrange
        String currency = "USD";

        // Act and Assert
        assertThrows(Exception.class, () -> handler.getExchangeRateSingleCurrency(currency, null));
    }

    @Test
    public void testGetExchangeRateSingleCurrency_WhenCurrencyIsNull() {
        // Arrange
        String date = "2024-01-16";

        // Act and Assert
        assertThrows(Exception.class, () -> handler.getExchangeRateSingleCurrency(null, date));
    }

    private String loadJsonFromFile(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream != null) {
                return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            } else {
                throw new IOException("Failed to load JSON file: " + fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while loading JSON file", e);
        }
    }
}
