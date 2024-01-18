import org.example.models.CurrencyExchange;
import org.example.models.CurrencyRate;
import org.example.services.ExchangeRateAPIHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    private ExchangeRateAPIHandler handler;

    @BeforeEach
    public void setup() {
        handler = new ExchangeRateAPIHandler(client);
    }

    @Test
    public void testGetExchangeRateSingleCurrency_ReturnsCorrectCurrencyExchange() throws Exception {
        // Arrange
        String currency = "USD";
        String date = "2024-01-16";
        String expectedResponse = "{\n" +
                "    \"table\": \"C\",\n" +
                "    \"currency\": \"dolar amerykański\",\n" +
                "    \"code\": \"USD\",\n" +
                "    \"rates\": [\n" +
                "        {\n" +
                "            \"no\": \"011/C/NBP/2024\",\n" +
                "            \"effectiveDate\": \"2024-01-16\",\n" +
                "            \"bid\": 3.9570,\n" +
                "            \"ask\": 4.0370\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        CurrencyExchange expectedCurrencyExchange = new CurrencyExchange();
        expectedCurrencyExchange.setCode(currency);
        CurrencyRate rate = new CurrencyRate();
        rate.setBuy(3.9570);
        rate.setSell(4.0370);
        expectedCurrencyExchange.setRates(Collections.singletonList(rate));

        when(response.body()).thenReturn(expectedResponse);
        when(response.statusCode()).thenReturn(200);
        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        // Act
        CurrencyExchange actualCurrencyExchange = handler.getExchangeRateSingleCurrency(currency, date);

        // Assert
        assertEquals(expectedCurrencyExchange.getCode(), actualCurrencyExchange.getCode());
        assertEquals(expectedCurrencyExchange.getRates().getFirst().getBuy(), actualCurrencyExchange.getRates().getFirst().getBuy());
        assertEquals(expectedCurrencyExchange.getRates().getFirst().getSell(), actualCurrencyExchange.getRates().getFirst().getSell());
        verify(client, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    public void testGetExchangeRateSingleCurrency_ThrowsException() throws Exception {
        // Arrange
        String currency = "USD";
        String date = "2024-01-16";

        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new IOException("Failed to send request"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            handler.getExchangeRateSingleCurrency(currency, date);
        });
    }

    @Test
    public void testGetExchangeRateSingleCurrency_ThrowsRuntimeException_WhenHttpResponseIsNotOk() throws Exception {
        // Arrange
        String currency = "USD";
        String date = "2024-01-16";

        when(response.statusCode()).thenReturn(201);
        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            handler.getExchangeRateSingleCurrency(currency, date);
        });
    }

    @Test
    public void testGetExchangeRateSingleCurrency_ThrowsRuntimeException_WhenIOExceptionOccurs() throws Exception {
        // Arrange
        String currency = "USD";
        String date = "2024-01-16";

        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(IOException.class);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            handler.getExchangeRateSingleCurrency(currency, date);
        });
    }

    @Test
    public void testGetExchangeRateSingleCurrency_WhenCurrencyIsEmpty() {
        // Arrange
        String currency = "";
        String date = "2024-01-16";

        // Act and Assert
        assertThrows(Exception.class, () -> {
            handler.getExchangeRateSingleCurrency(currency, date);
        });
    }

    @Test
    public void testGetExchangeRateSingleCurrency_WhenDateIsEmpty() {
        // Arrange
        String currency = "USD";
        String date = "";

        // Act and Assert
        assertThrows(Exception.class, () -> {
            handler.getExchangeRateSingleCurrency(currency, date);
        });
    }
}
