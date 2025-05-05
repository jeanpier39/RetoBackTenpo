package challenge.tenpo.tenpo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

//    private Double cachedPercentage = null;
//    private Instant cacheTimestamp = null;
//    private final long CACHE_DURATION_MINUTES = 30;

    public PercentageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//    public double getPercentage() {
//        if (cachedPercentage != null && cacheTimestamp != null &&
//                Instant.now().isBefore(cacheTimestamp.plusSeconds(CACHE_DURATION_MINUTES * 10))) {
//            return cachedPercentage;
//        }
//
//        try {
//            double external = fetchFromExternal();
//            cachedPercentage = external;
//            cacheTimestamp = Instant.now();
//            return external;
//        } catch (Exception e) {
//            if (cachedPercentage != null) return cachedPercentage;
//            throw new RuntimeException("No se pudo obtener el porcentaje externo.");
//        }
//    }
//
//    public double fetchFromExternal() {
//        try {
//            // Carga el recurso desde classpath
//            ClassPathResource resource = new ClassPathResource("/json/mockPorcentage.json");
//            String json = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
//
//            // Parsea el JSON y extrae el campo "percentage"
//            JsonNode root = objectMapper.readTree(json);
//            log.info("Porcentaje: {}", root.get("porcentaje").asDouble());
//            return root.get("porcentaje").asDouble();
//
//        } catch (IOException e) {
//            throw new RuntimeException("No se pudo leer mockPercentage.json", e);
//        }
//    }


    /**
     * Este valor se cachea durante 30m (según CacheConfig).
     * Si la llamada falla, lanza excepción y Spring no actualizará el cache,
     * por lo que se seguirá usando el antiguo valor hasta que expire.
     */
    @Cacheable("percentage")
    public double getPercentage() {
        return fetchFromExternal();
    }

    public double fetchFromExternal() {
        try {
            ClassPathResource resource = new ClassPathResource("/json/mockPorcentage.json");
            String json = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            JsonNode root = objectMapper.readTree(json);
            double pct = root.get("porcentaje").asDouble();
            log.info("Porcentaje externo leido: {}", pct);
            return pct;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer mockPercentage.json", e);
        }
    }

}
