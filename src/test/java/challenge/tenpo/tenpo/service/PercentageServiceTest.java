package challenge.tenpo.tenpo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PercentageServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PercentageService percentageService;

    @Test
    public void testGetPercentage_ServiceIsActive() throws Exception {
        double valueExpected= 20.0;

        ObjectMapper realMapper = new ObjectMapper();
        String jsonString = "{\"serviceIsActive\": true, \"porcentaje\": 20.0}";
        JsonNode parsedNode = realMapper.readTree(jsonString);

        PercentageService spyService = spy(new PercentageService(objectMapper));
        doReturn(parsedNode).when(spyService).loadExternalPercentageJson();

        double result = spyService.getPercentage();
        assertEquals(valueExpected, result);
    }

    @Test
    public void testGetPercentage_ServiceIsNotActive() throws Exception {
        double valueExpected= 20.0;

        ObjectMapper realMapper = new ObjectMapper();
        String jsonString = "{\"serviceIsActive\": false}";
        JsonNode parsedNode = realMapper.readTree(jsonString);

        PercentageService spyService = spy(new PercentageService(objectMapper));
        doReturn(parsedNode).when(spyService).loadExternalPercentageJson();
        doReturn(valueExpected).when(spyService).getCachedPercentage();

        // Act
        double result = spyService.getPercentage();

        // Assert
        assertEquals(valueExpected, result);
    }

    @Test
    public void testGetPercentage_ServiceIsNotActiveNotCache() throws Exception {
        // Arrange
        ObjectMapper realMapper = new ObjectMapper();
        String jsonString = "{\"serviceIsActive\": false}";
        JsonNode parsedNode = realMapper.readTree(jsonString);

        PercentageService spyService = spy(new PercentageService(objectMapper));
        doReturn(parsedNode).when(spyService).loadExternalPercentageJson();
        // No mockeamos getCachedPercentage, así que lanzará la excepción real

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, spyService::getPercentage);

        assertEquals("No se pudo obtener el porcentaje desde la caché.", exception.getMessage());
    }
}