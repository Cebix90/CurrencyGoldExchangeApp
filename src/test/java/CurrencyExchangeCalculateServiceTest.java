import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.handlers.ExchangeRateAPIHandler;
import org.currencygoldexchangeapp.services.CurrencyExchangeCalculateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyExchangeCalculateServiceTest {
    @Mock
    private ExchangeRateAPIHandler exchangeRateAPIHandler;
    @InjectMocks
    private CurrencyExchangeCalculateService currencyExchangeService;

    @Test
    public void testCalculateExchangeAmountForPLN() {
        // Arrange
        String sourceCurrency = "USD";
        double amount = 100.0;
        String targetCurrency = "PLN";
        String date = "2024-01-16";

        CurrencyExchange sourceCurrencyExchange = new CurrencyExchange();
        initializeCurrencyExchange(sourceCurrencyExchange, sourceCurrency, 3.9570, 4.0370);

        when(exchangeRateAPIHandler.getExchangeRateSingleCurrency(sourceCurrency, date)).thenReturn(sourceCurrencyExchange);

        currencyExchangeService = new CurrencyExchangeCalculateService(exchangeRateAPIHandler);

        // Act
        CurrencyExchange result = currencyExchangeService.calculateExchangeAmount(sourceCurrency, amount, targetCurrency, date);

        // Assert
        assertEquals(result.getCode(), "USD");
        assertEquals(395.7, result.getBid() );
        assertEquals(403.7, result.getAsk() );
    }

    @Test
    public void testCalculateExchangeAmountForPLN_WhenTargetCurrencyIsEmpty() {
        // Arrange
        String sourceCurrency = "USD";
        double amount = 100.0;
        String targetCurrency = "";
        String date = "2024-01-16";

        CurrencyExchange sourceCurrencyExchange = new CurrencyExchange();
        initializeCurrencyExchange(sourceCurrencyExchange, sourceCurrency, 3.9570, 4.0370);

        when(exchangeRateAPIHandler.getExchangeRateSingleCurrency(sourceCurrency, date)).thenReturn(sourceCurrencyExchange);

        currencyExchangeService = new CurrencyExchangeCalculateService(exchangeRateAPIHandler);

        // Act
        CurrencyExchange result = currencyExchangeService.calculateExchangeAmount(sourceCurrency, amount, targetCurrency, date);

        // Assert
        assertEquals(result.getCode(), "USD");
        assertEquals(395.7, result.getBid() );
        assertEquals(403.7, result.getAsk() );
    }

    @Test
    public void testCalculateExchangeAmountForOtherCurrency() {
        // Arrange
        String sourceCurrency = "USD";
        double amount = 100.0;
        String targetCurrency = "JPY";
        String date = "2024-01-16";

        CurrencyExchange sourceCurrencyExchange = new CurrencyExchange();
        initializeCurrencyExchange(sourceCurrencyExchange, sourceCurrency, 3.9570, 4.0370);

        CurrencyExchange targetCurrencyExchange = new CurrencyExchange();
        initializeCurrencyExchange(targetCurrencyExchange, targetCurrency, 0.027125, 0.027673);

        when(exchangeRateAPIHandler.getExchangeRateSingleCurrency(sourceCurrency, date)).thenReturn(sourceCurrencyExchange);
        when(exchangeRateAPIHandler.getExchangeRateSingleCurrency(targetCurrency, date)).thenReturn(targetCurrencyExchange);

        currencyExchangeService = new CurrencyExchangeCalculateService(exchangeRateAPIHandler);

        // Act
        CurrencyExchange result = currencyExchangeService.calculateExchangeAmount(sourceCurrency, amount, targetCurrency, date);

        // Assert
        assertEquals(result.getCode(), "USD");
        assertEquals(14588.0184, result.getBid() );
        assertEquals(14588.2268, result.getAsk() );
    }

    @Test
    public void testCalculateExchangeAmountThrowsExceptionWhenSourceCurrencyIsNull() {
        // Arrange
        String sourceCurrency = null;
        double amount = 100.0;
        String targetCurrency = "PLN";
        String date = "2024-01-16";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                currencyExchangeService.calculateExchangeAmount(sourceCurrency, amount, targetCurrency, date));

        assertEquals("Source currency must not be null", exception.getMessage());
    }

    @Test
    public void testCalculateExchangeAmountThrowsExceptionWhenAmountIsSmallerThanOne() {
        // Arrange
        String sourceCurrency = "USD";
        double amount = 0;
        String targetCurrency = "";
        String date = "2024-01-16";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                currencyExchangeService.calculateExchangeAmount(sourceCurrency, amount, targetCurrency, date));

        assertEquals("Amount must be a positive number", exception.getMessage());
    }

    private static void initializeCurrencyExchange(CurrencyExchange sourceCurrencyExchange, String sourceCurrency, double bid, double ask) {
        sourceCurrencyExchange.setCode(sourceCurrency);
        sourceCurrencyExchange.setBid(bid);
        sourceCurrencyExchange.setAsk(ask);
    }
}
