import org.apache.commons.io.IOUtils;
import org.currencygoldexchangeapp.datamodels.GoldValue;
import org.currencygoldexchangeapp.exceptions.DataNotFoundException;
import org.currencygoldexchangeapp.exceptions.ExceededResultsLimitException;
import org.currencygoldexchangeapp.handlers.GoldValueAPIHandler;
import org.junit.jupiter.api.Nested;
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
import java.util.Arrays;
import java.util.List;

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

    @Nested
    class TestGetGoldValueForSpecificDate {
        @Test
        public void returnsCorrectGoldValue() {
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
        public void throwsException() throws Exception {
            // Arrange
            String date = "2024-02-29";

            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenThrow(new IOException("Failed to send request"));

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getGoldValueForSpecificDate(date));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs201() throws Exception {
            // Arrange
            String date = "2024-02-29";

            when(response.statusCode()).thenReturn(201);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getGoldValueForSpecificDate(date));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs400() throws Exception {
            // Arrange
            String date = "2024-02-29";

            when(response.statusCode()).thenReturn(400);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(ExceededResultsLimitException.class, () -> handler.getGoldValueForSpecificDate(date));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs404() throws Exception {
            // Arrange
            String date = "2024-02-29";

            when(response.statusCode()).thenReturn(404);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(DataNotFoundException.class, () -> handler.getGoldValueForSpecificDate(date));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs408() throws Exception {
            // Arrange
            String date = "2024-02-29";

            when(response.statusCode()).thenReturn(408);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getGoldValueForSpecificDate(date));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs500() throws Exception {
            // Arrange
            String date = "2024-02-29";

            when(response.statusCode()).thenReturn(500);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getGoldValueForSpecificDate(date));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs503() throws Exception {
            // Arrange
            String date = "2024-02-29";

            when(response.statusCode()).thenReturn(503);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getGoldValueForSpecificDate(date));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs504() throws Exception {
            // Arrange
            String date = "2024-02-29";

            when(response.statusCode()).thenReturn(504);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getGoldValueForSpecificDate(date));
        }

        @Test
        public void throwsRuntimeException_WhenIOExceptionOccurs() throws Exception {
            // Arrange
            String date = "2024-02-29";

            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenThrow(IOException.class);

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getGoldValueForSpecificDate(date));
        }

        @Test
        public void whenDateIsEmpty() {
            // Arrange
            String date = "";

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getGoldValueForSpecificDate(date));
        }

        @Test
        public void whenDateIsNull() {
            // Arrange, Act and Assert
            assertThrows(DataNotFoundException.class, () -> handler.getGoldValueForSpecificDate(null));
        }
    }

    @Nested
    class TestGetLastGoldValues {
        @Test
        public void returnsCorrectListOfGoldValues() {
            // Arrange
            List<GoldValue> expectedGoldValues = Arrays.asList(
                    createGoldValue("2024-02-29", 260.85),
                    createGoldValue("2024-03-01", 262.10),
                    createGoldValue("2024-03-04", 263.09),
                    createGoldValue("2024-03-05", 268.59),
                    createGoldValue("2024-03-06", 273.37)
            );

            String expectedResponse = loadJsonFromFile("last_gold_values_response.json");

            when(response.body()).thenReturn(expectedResponse);
            when(response.statusCode()).thenReturn(200);
            try {
                when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Act
            List<GoldValue> actualGoldValues = handler.getLastGoldValues(5);

            // Assert
            for (int i = 0; i < expectedGoldValues.size(); i++) {
                assertEquals(expectedGoldValues.get(i).getEffectiveDate(), actualGoldValues.get(i).getEffectiveDate());
                assertEquals(expectedGoldValues.get(i).getValue(), actualGoldValues.get(i).getValue());
            }

            try {
                verify(client, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        public void throwsException() throws Exception {
            // Arrange
            int topCount = 22;

            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenThrow(new IOException("Failed to send request"));

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getLastGoldValues(topCount));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs201() throws Exception {
            // Arrange
            int topCount = 22;

            when(response.statusCode()).thenReturn(201);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getLastGoldValues(topCount));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs400() throws Exception {
            // Arrange
            int topCount = 22;

            when(response.statusCode()).thenReturn(400);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(ExceededResultsLimitException.class, () -> handler.getLastGoldValues(topCount));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs404() throws Exception {
            // Arrange
            int topCount = 22;

            when(response.statusCode()).thenReturn(404);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(DataNotFoundException.class, () -> handler.getLastGoldValues(topCount));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs408() throws Exception {
            // Arrange
            int topCount = 22;

            when(response.statusCode()).thenReturn(408);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getLastGoldValues(topCount));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs500() throws Exception {
            // Arrange
            int topCount = 22;

            when(response.statusCode()).thenReturn(500);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getLastGoldValues(topCount));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs503() throws Exception {
            // Arrange
            int topCount = 22;

            when(response.statusCode()).thenReturn(503);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getLastGoldValues(topCount));
        }

        @Test
        public void throwsRuntimeException_WhenHttpResponseIs504() throws Exception {
            // Arrange
            int topCount = 22;

            when(response.statusCode()).thenReturn(504);
            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getLastGoldValues(topCount));
        }

        @Test
        public void throwsRuntimeException_WhenIOExceptionOccurs() throws Exception {
            // Arrange
            int topCount = 22;

            when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenThrow(IOException.class);

            // Act and Assert
            assertThrows(RuntimeException.class, () -> handler.getLastGoldValues(topCount));
        }

        @Test
        public void whenTopCountIsNegativeOrZero() {
            // Arrange
            int topCountNegative = -1;
            int topCountZero = 0;

            // Act and Assert
            assertThrows(DataNotFoundException.class, () -> handler.getLastGoldValues(topCountNegative));
            assertThrows(DataNotFoundException.class, () -> handler.getLastGoldValues(topCountZero));
        }

        @Test
        public void whenTopCountIs0() {
            // Arrange, Act and Assert
            assertThrows(DataNotFoundException.class, () -> handler.getLastGoldValues(null));
        }
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

    private GoldValue createGoldValue(String effectiveDate, double value) {
        GoldValue goldValue = new GoldValue();
        goldValue.setEffectiveDate(effectiveDate);
        goldValue.setValue(value);
        return goldValue;
    }
}
