package challenge.tenpo.tenpo.service;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PercentageService {
    private Double cachedPercentage = null;
    private Instant cacheTimestamp = null;
    private final long CACHE_DURATION_MINUTES = 30;

    public double getPercentage() {
        if (cachedPercentage != null && cacheTimestamp != null &&
                Instant.now().isBefore(cacheTimestamp.plusSeconds(CACHE_DURATION_MINUTES * 60))) {
            return cachedPercentage;
        }

        try {
            double external = fetchFromExternal();
            cachedPercentage = external;
            cacheTimestamp = Instant.now();
            return external;
        } catch (Exception e) {
            if (cachedPercentage != null) return cachedPercentage;
            throw new RuntimeException("No se pudo obtener el porcentaje externo.");
        }
    }

    private double fetchFromExternal() {
        // Simula el llamado (mock fijo)
        return 10.0;
    }

}
