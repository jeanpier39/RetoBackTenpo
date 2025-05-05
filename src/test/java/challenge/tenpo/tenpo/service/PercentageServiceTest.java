package challenge.tenpo.tenpo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(
        classes = {
                PercentageService.class,
                PercentageServiceTest.TestCacheConfig.class,
                ObjectMapper.class
        }
)
@ActiveProfiles("test")
class PercentageServiceTest {

    @Configuration
    static class TestCacheConfig {
        @Bean
        CacheManager cacheManager() {
            CaffeineCacheManager cm = new CaffeineCacheManager("percentage");
            cm.setCaffeine(
                    Caffeine.newBuilder()
                            .expireAfterWrite(30, TimeUnit.MINUTES)
                            .maximumSize(1)
            );
            return cm;
        }
    }

    @Autowired
    PercentageService percentageService;

    @MockitoSpyBean
    PercentageService spyPercentageService;  // para verificar invocaciones

    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        cacheManager.getCache("percentage").clear();
    }

    @Test
    void cuandoExternalFunciona_seCacheaYDevuelveMismoValor() {
        // 1ª llamada → lee JSON, cachea
        double v1 = percentageService.getPercentage();
        // 2ª llamada → saca de cache
        double v2 = percentageService.getPercentage();

        assertThat(v1).isEqualTo(v2);
        // fetchFromExternal() debería haberse llamado solo 1 vez
        verify(spyPercentageService, times(1)).fetchFromExternal();
    }

    @Test
    void cuandoExternalFalla_devuelveValorCacheado() {
        // Forzamos primer valor:
        doReturn(10.0).when(spyPercentageService).fetchFromExternal();
        double primero = percentageService.getPercentage();
        assertThat(primero).isEqualTo(10.0);

        // Ahora forzamos fallo externo
        doThrow(new RuntimeException("external error"))
                .when(spyPercentageService).fetchFromExternal();

        // Como ya hay cache, seguimos obteniendo 10.0
        double segundo = percentageService.getPercentage();
        assertThat(segundo).isEqualTo(10.0);

        // Verificamos que se intentó fetch dos veces (1 OK + 1 fallo)
        verify(spyPercentageService, times(2)).fetchFromExternal();
    }

    @Test
    void cuandoNoHayCacheYExternalFalla_lanzaExcepcion() {
        // cache limpio y fallo inmediato
        doThrow(new RuntimeException("external missing"))
                .when(spyPercentageService).fetchFromExternal();

        assertThatThrownBy(() -> percentageService.getPercentage())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("external missing");
    }
}