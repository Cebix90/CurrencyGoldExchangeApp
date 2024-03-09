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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoldValueCalculateServiceTest {
    @Mock
    private GoldValueAPIHandler goldValueAPIHandler;
    @InjectMocks
    private GoldValueCalculateService goldValueCalculateService;

    @Test
    public void testCalculateGainOrLoss_WorkingDay() {
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
        Optional<BigDecimal> result = goldValueCalculateService.calculateGainOrLoss(startDate, endDate);

        BigDecimal resultBigDecimal = BigDecimal.valueOf(0.00);
        if(result.isPresent()) {
            resultBigDecimal = result.get();
        }

        // Assert
        assertEquals(expectedGainOrLoss, resultBigDecimal);
    }

    @Test
    public void testCalculateGainOrLoss_WeekendDay() {
        // Arrange
        String startDate = "2024-02-29";
        String endDate = "2024-03-09";
        BigDecimal expectedGainOrLoss = new BigDecimal("-0.02");

        List<GoldValue> mockGoldValues = Arrays.asList(
                createGoldValue("2024-02-29", 260.85),
                createGoldValue("2024-03-01", 262.10),
                createGoldValue("2024-03-04", 263.09),
                createGoldValue("2024-03-05", 268.59),
                createGoldValue("2024-03-06", 273.37),
                createGoldValue("2024-03-07", 273.01),
                createGoldValue("2024-03-08", 273.35)
        );

        when(goldValueAPIHandler.getGoldValuesForDateRange(startDate, endDate)).thenReturn(mockGoldValues);

        goldValueCalculateService = new GoldValueCalculateService(goldValueAPIHandler);

        // Act
        Optional<BigDecimal> result = goldValueCalculateService.calculateGainOrLoss(startDate, endDate);

        BigDecimal resultBigDecimal = BigDecimal.valueOf(0.00);
        if(result.isPresent()) {
            resultBigDecimal = result.get();
        }

        // Assert
        assertEquals(expectedGainOrLoss, resultBigDecimal);
    }

    @Test
    public void testCalculateGainOrLoss_HolidayDay() {
        // Arrange
        String startDate = "2024-01-02";
        String endDate = "2024-01-06";
        BigDecimal expectedGainOrLoss = new BigDecimal("0.00");

        List<GoldValue> mockGoldValues = Arrays.asList(
                createGoldValue("2024-01-02", 260.85),
                createGoldValue("2024-01-03", 262.10),
                createGoldValue("2024-01-04", 263.09),
                createGoldValue("2024-01-05", 268.59)
        );

        when(goldValueAPIHandler.getGoldValuesForDateRange(startDate, endDate)).thenReturn(mockGoldValues);

        goldValueCalculateService = new GoldValueCalculateService(goldValueAPIHandler);

        // Act
        Optional<BigDecimal> result = goldValueCalculateService.calculateGainOrLoss(startDate, endDate);

        BigDecimal resultBigDecimal = BigDecimal.valueOf(0.00);
        if(result.isPresent()) {
            resultBigDecimal = result.get();
        }

        // Assert
        assertEquals(expectedGainOrLoss, resultBigDecimal);
    }

    private GoldValue createGoldValue(String effectiveDate, double value) {
        GoldValue goldValue = new GoldValue();
        goldValue.setEffectiveDate(effectiveDate);
        goldValue.setValue(value);
        return goldValue;
    }
}
