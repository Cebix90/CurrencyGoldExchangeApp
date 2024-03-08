import org.currencygoldexchangeapp.datamodels.GoldValue;
import org.currencygoldexchangeapp.handlers.GoldValueAPIHandler;
import org.currencygoldexchangeapp.services.GoldValueCalculateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoldValueCalculateServiceTest {
    @Mock
    private GoldValueAPIHandler goldValueAPIHandler;
    @InjectMocks
    private GoldValueCalculateService goldValueCalculateService;

    @Test
    public void testCalculateGainOrLoss() {
        // Arrange
        String startDate = "2024-02-29";
        String endDate = "2024-03-06";
        BigDecimal expectedGainOrLoss = new BigDecimal("4.78");

        List<GoldValue> mockGoldValues = Arrays.asList(
                createGoldValue("2024-02-29", 260.85),
                createGoldValue("2024-03-01", 262.10),
                createGoldValue("2024-03-04", 263.09),
                createGoldValue("2024-03-05", 268.59),
                createGoldValue("2024-03-06", 273.37)
        );

        when(goldValueAPIHandler.getGoldValuesForDateRange(startDate, endDate)).thenReturn(mockGoldValues);

        goldValueCalculateService = new GoldValueCalculateService(goldValueAPIHandler);

        // Act
        BigDecimal result = goldValueCalculateService.calculateGainOrLoss(startDate, endDate);

        // Assert
        assertEquals(expectedGainOrLoss, result);
    }

    private GoldValue createGoldValue(String effectiveDate, double value) {
        GoldValue goldValue = new GoldValue();
        goldValue.setEffectiveDate(effectiveDate);
        goldValue.setValue(value);
        return goldValue;
    }
}
