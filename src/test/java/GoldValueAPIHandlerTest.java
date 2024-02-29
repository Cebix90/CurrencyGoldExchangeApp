import org.apache.commons.io.IOUtils;
import org.currencygoldexchangeapp.datamodels.GoldValue;
import org.currencygoldexchangeapp.handlers.GoldValueAPIHandler;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoldValueAPIHandlerTest {
    @Mock
    private HttpClient client;

    @Mock
    private HttpResponse<String> response;

    @InjectMocks
    private GoldValueAPIHandler handler;

    @Test
    public void testGetGoldValueForSpecificDate_ReturnsCorrectGoldValue() {
        // Arrange
        double value = 260.85;
        String date = "2024-02-29";
        String expectedResponse = loadJsonFromFile("single_gold_value_response.json");

        GoldValue expectedGoldValue = new GoldValue();
        expectedGoldValue.setValue(value);

        when(response.body()).thenReturn(expectedResponse);
        when(response.statusCode()).thenReturn(200);
        try {
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Act
        GoldValue actualGoldValue = handler.getGoldValueForSpecificDate(date);

        // Assert
        assertEquals(expectedGoldValue.getValue(), actualGoldValue.getValue());

        try {
            verify(client, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetGoldValueForSpecificDate_ThrowsException() throws Exception {
        // Arrange
        String date = "2024-02-29";

        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenThrow(new IOException("Failed to send request"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> handler.getGoldValueForSpecificDate(date));
    }

    @Test
    public void testGetGoldValueForSpecificDate_ThrowsRuntimeException_WhenHttpResponseIsNotOk() throws Exception {
        // Arrange
        String date = "2024-02-29";

        when(response.statusCode()).thenReturn(201);
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> handler.getGoldValueForSpecificDate(date));
    }

    @Test
    public void testGetGoldValueForSpecificDate_ThrowsRuntimeException_WhenIOExceptionOccurs() throws Exception {
        // Arrange
        String date = "2024-02-29";

        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenThrow(IOException.class);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> handler.getGoldValueForSpecificDate(date));
    }

    @Test
    public void testGetExchangeRateSingleCurrency_WhenDateIsEmpty() {
        // Arrange
        String date = "";

        // Act and Assert
        assertThrows(Exception.class, () -> handler.getGoldValueForSpecificDate(date));
    }

    @Test
    public void testGetExchangeRateSingleCurrency_WhenDateIsNull() {
        // Arrange, Act and Assert
        assertThrows(Exception.class, () -> handler.getGoldValueForSpecificDate(null));
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
