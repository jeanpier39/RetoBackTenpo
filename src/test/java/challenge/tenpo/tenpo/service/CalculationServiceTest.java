package challenge.tenpo.tenpo.service;

import challenge.tenpo.tenpo.repository.LogEntryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CalculationServiceTest {
    @MockitoBean
    PercentageService percentageService;

    @MockitoBean
    LogEntryRepository repo;

    @Autowired
    CalculationService service;

    @Test
    public void testCalculateCorrectly() {
        Mockito.when(percentageService.getPercentage()).thenReturn(10.0);
        double result = service.calculate(100, 50);
        assertEquals(165.0, result);
    }
}
