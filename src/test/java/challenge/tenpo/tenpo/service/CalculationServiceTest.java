package challenge.tenpo.tenpo.service;

import challenge.tenpo.tenpo.entity.LogEntry;
import challenge.tenpo.tenpo.repository.LogEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculationServiceTest {
    @Mock
    private PercentageService percentageService;

    @Mock
    private LogEntryRepository logRepo;

    @InjectMocks
    private CalculationService calculationService;

    @Test
    void testCalculate_successful() throws InterruptedException {
        double valueExpected= 165.0;
        // Arrange
        when(percentageService.getPercentage()).thenReturn(10.0);

        // Act
        double result = calculationService.calculate(100, 50); // 150 + 10% = 165

        // Assert
        assertEquals(valueExpected, result);

        TimeUnit.MILLISECONDS.sleep(200);

        ArgumentCaptor<LogEntry> captor = ArgumentCaptor.forClass(LogEntry.class);
        verify(logRepo, atLeastOnce()).save(captor.capture());

        LogEntry logEntry = captor.getValue();
        assertEquals("/api/calculate", logEntry.getEndpoint());
        assertTrue(logEntry.getParameters().contains("num1=100"));
        assertEquals("165.0", logEntry.getResponse());
        assertNull(logEntry.getError());
        assertNotNull(logEntry.getTimestamp());
    }

    @Test
    void testCalculate_error() throws InterruptedException {
        // Arrange
        when(percentageService.getPercentage()).thenThrow(new RuntimeException("Service unavailable"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            calculationService.calculate(10, 20);
        });
        assertEquals("Service unavailable", exception.getMessage());

        TimeUnit.MILLISECONDS.sleep(200);

        ArgumentCaptor<LogEntry> captor = ArgumentCaptor.forClass(LogEntry.class);
        verify(logRepo, atLeastOnce()).save(captor.capture());

        LogEntry logEntry = captor.getValue();
        assertEquals("0.0", logEntry.getResponse());
        assertEquals("Service unavailable", logEntry.getError());
    }
}
