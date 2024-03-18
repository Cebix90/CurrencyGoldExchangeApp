import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Scanner;

import static org.currencygoldexchangeapp.utils.InputUtility.*;
import static org.junit.jupiter.api.Assertions.*;

public class InputUtilityTest {
    @Nested
    class TestGetUserInputForCurrency{
        @Test
        public void sourceCurrencyAsUSD() {
            // Arrange
            String inputData = "USD";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            String result = getUserInputForCurrency(scanner, "Enter currency: ", "2024-03-14", true);

            // Assert
            assertEquals("USD", result);
            System.setIn(System.in);
        }

        @Test
        public void targetCurrencyAsPLN() {
            // Arrange
            String inputData = "PLN";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            String result = getUserInputForCurrency(scanner, "Enter currency: ", "2024-03-14", false);

            // Assert
            assertEquals("PLN", result);
            System.setIn(System.in);
        }

        @Test
        public void targetCurrencyAsJPY() {
            // Arrange
            String inputData = "JPY";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            String result = getUserInputForCurrency(scanner, "Enter currency: ", "2024-03-14", false);

            // Assert
            assertEquals("JPY", result);
            System.setIn(System.in);
        }
    }

    @Nested
    class TestIsCurrencyAvailable {
        @Test
        public void forYear2011AndAfter() {
            // Arrange
            String currencyAvailable = "USD";
            String currencyNotAvailable = "EEK";
            String date = "2024-03-14";

            // Act
            boolean resultForCurrencyAvailable = isCurrencyAvailable(currencyAvailable, date);
            boolean resultForCurrencyNotAvailable = isCurrencyAvailable(currencyNotAvailable, date);

            // Assert
            assertTrue(resultForCurrencyAvailable, "USD currency should be available");
            assertFalse(resultForCurrencyNotAvailable, "EEK currency shouldn't be available");
        }

        @Test
        public void forYears2003Till2010() {
            // Arrange
            String currencyAvailable = "USD";
            String currencyNotAvailable = "EEK";
            String date = "2008-03-14";

            // Act
            boolean resultForCurrencyAvailable = isCurrencyAvailable(currencyAvailable, date);
            boolean resultForCurrencyNotAvailable = isCurrencyAvailable(currencyNotAvailable, date);

            // Assert
            assertTrue(resultForCurrencyAvailable, "USD currency should be available");
            assertTrue(resultForCurrencyNotAvailable, "EEK currency should be available");
        }

        @Test
        public void testIsCurrencyAvailableForYearBefore2003() {
            // Arrange
            String currency = "USD";
            String date = "2002-12-31";

            // Act
            boolean result = isCurrencyAvailable(currency, date);

            // Assert
            assertFalse(result);
        }

        @Test
        public void forUnavailableCurrency() {
            // Arrange
            String currency = "XYZ";
            String date = "2024-03-14";

            // Act
            boolean result = isCurrencyAvailable(currency, date);

            // Assert
            assertFalse(result);
        }
    }

    @Nested
    class TestGetDate {
        @Test
        public void forCurrencies() {
            // Arrange
            String inputData = "2024-03-14\n";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            LocalDate result = getDateForCurrencies(scanner);

            // Assert
            assertEquals(LocalDate.parse("2024-03-14"), result, "Should return 2024-03-14 date");
            System.setIn(System.in);
        }

        @Test
        public void forCurrenciesLoop() {
            // Arrange
            String inputData = "2002-12-31\n2024-03-14\n";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            LocalDate result = getDateForCurrencies(scanner);

            // Assert
            assertEquals(LocalDate.parse("2024-03-14"), result, "Should return 2024-03-14 date");
            System.setIn(System.in);
        }

        @Test
        public void forGold() {
            // Arrange
            String inputData = "2024-03-14\n";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            LocalDate result = getDateForGold(scanner);

            // Assert
            assertEquals(LocalDate.parse("2024-03-14"), result, "Should return 2024-03-14 date");
            System.setIn(System.in);
        }

        @Test
        public void forGoldLoop() {
            // Arrange
            String inputData = "2012-12-31\n2024-03-14\n";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            LocalDate result = getDateForGold(scanner);

            // Assert
            assertEquals(LocalDate.parse("2024-03-14"), result, "Should return 2024-03-14 date");
            System.setIn(System.in);
        }

        @Test
        public void forCurrenciesEmptyInput() {
            // Arrange
            String inputData = "\n";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            LocalDate result = getDateForCurrencies(scanner);

            // Assert
            assertEquals(LocalDate.now(), result, "Should return today's date");
            System.setIn(System.in);
        }

        @Test
        public void forCurrenciesFutureDate() {
            // Arrange
            String futureDate = LocalDate.now().plusDays(1).toString();
            String inputData = futureDate + "\n2024-03-14\n";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            LocalDate result = getDateForCurrencies(scanner);

            // Assert
            assertEquals(LocalDate.parse("2024-03-14"), result, "Should return 2024-03-14 date");
            System.setIn(System.in);
        }

        @Test
        public void forCurrenciesInvalidFormat() {
            // Arrange
            String inputData = "invalid format\n2024-03-14\n";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            LocalDate result = getDateForCurrencies(scanner);

            // Assert
            assertEquals(LocalDate.parse("2024-03-14"), result, "Should return 2024-03-14 date");
            System.setIn(System.in);
        }
    }

    @Nested
    class TestGetCustomAmount {
        @Test
        public void withCorrectInput() {
            // Arrange
            String inputData = "10\n";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            double result = getCustomAmount(scanner);

            // Assert
            assertEquals(10.00, result, 0.001, "Should return 10.00");
            System.setIn(System.in);
        }

        @Test
        public void withEmptyInput() {
            // Arrange
            String inputData = "\n";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            double result = getCustomAmount(scanner);

            // Assert
            assertEquals(1.00, result, 0.001, "Should return 1.00");
            System.setIn(System.in);
        }

        @Test
        public void withInvalidInput() {
            // Arrange
            String inputData = "invalid\n10\n";
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
            System.setIn(inputStream);
            Scanner scanner = new Scanner(System.in);

            // Act
            double result = getCustomAmount(scanner);

            // Assert
            assertEquals(10.00, result, 0.001, "Should return 1.00");
            System.setIn(System.in);
        }
    }
}
