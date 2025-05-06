package challenge.tenpo.tenpo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class PercentageService {

    private final ObjectMapper objectMapper;

    public PercentageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public double getPercentage() {
        JsonNode root = loadExternalPercentageJson();

        if (!isServiceActive(root)) {
            log.warn("El servicio no está activo");
            // Recupera el valor desde caché
            return getCachedPercentage();
        }

        double percentage = extractPercentage(root);
        log.info("Porcentaje externo leído: {}", percentage);
        return cachePercentage(percentage); // Almacena en caché
    }

    @Cacheable(value = "percentageCache", key = "'percentage'")
    public double getCachedPercentage() {
        throw new RuntimeException("No se pudo obtener el porcentaje desde la caché.");
    }

    @CachePut(value = "percentageCache", key = "'percentage'")
    public double cachePercentage(double percentage) {
        return percentage;
    }

    JsonNode loadExternalPercentageJson() {
        return readJsonFromFile("/json/mockPorcentage.json");
    }

    private boolean isServiceActive(JsonNode root) {
        return root.path("serviceIsActive").asBoolean(false);
    }

    private double extractPercentage(JsonNode root) {
        return root.path("porcentaje").asDouble();
    }

    private JsonNode readJsonFromFile(String filePath) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            String json = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return objectMapper.readTree(json);
        } catch (IOException e) {
            log.error("Error reading JSON file: {}", filePath, e);
            throw new RuntimeException("Error reading JSON file", e);
        }
    }

}
