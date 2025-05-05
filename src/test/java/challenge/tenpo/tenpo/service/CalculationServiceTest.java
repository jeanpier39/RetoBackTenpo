package challenge.tenpo.tenpo.service;

import challenge.tenpo.tenpo.entity.LogEntry;
import challenge.tenpo.tenpo.repository.LogEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    @Mock
    PercentageService percentageService;
    @Mock
    LogEntryRepository logRepo;

    CalculationService service;
    ExecutorService directExecutor;

    @BeforeEach
    void setUp() {
        // Executor que corre la tarea inmediatamente en el mismo hilo
        directExecutor = new AbstractExecutorService() {
            @Override public void execute(Runnable command) { command.run(); }
            @Override public void shutdown() { }
            @Override public List<Runnable> shutdownNow() { return Collections.emptyList(); }
            @Override public boolean isShutdown() { return false; }
            @Override public boolean isTerminated() { return false; }
            @Override public boolean awaitTermination(long timeout, TimeUnit unit) { return true; }
        };
        service = new CalculationService(percentageService, logRepo, directExecutor);
    }

    @Test
    void calculate_success_logsAndReturnsCorrectValue() {
        when(percentageService.getPercentage()).thenReturn(10.0);
        double result = service.calculate(100.0, 50.0);
        assertThat(result).isEqualTo(165.0);

        ArgumentCaptor<LogEntry> cap = ArgumentCaptor.forClass(LogEntry.class);
        verify(logRepo).save(cap.capture());

        LogEntry entry = cap.getValue();
        assertThat(entry.getEndpoint()).isEqualTo("/api/calculate");
        assertThat(entry.getParameters()).isEqualTo("num1=100.0&num2=50.0");
        assertThat(entry.getResponse()).isEqualTo("165.0");
        assertThat(entry.getError()).isNull();
        assertThat(entry.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void calculate_externalFailure_throwsAndLogsError() {
        when(percentageService.getPercentage())
                .thenThrow(new RuntimeException("servicio caído"));
        Throwable thrown = catchThrowable(() -> service.calculate(1.0, 1.0));
        assertThat(thrown)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("servicio caído");

        ArgumentCaptor<LogEntry> cap = ArgumentCaptor.forClass(LogEntry.class);
        verify(logRepo).save(cap.capture());

        LogEntry entry = cap.getValue();
        assertThat(entry.getResponse()).isEqualTo("0.0");
        assertThat(entry.getError()).isEqualTo("servicio caído");
    }

    @Test
    void getLogs_delegatesToRepository() {
        Pageable pageReq = PageRequest.of(0, 5);

        Page<LogEntry> fakePage = new PageImpl<>(List.of(new LogEntry()));

        when(logRepo.findAll(pageReq)).thenReturn(fakePage);

        Page<LogEntry> page = service.getLogs(pageReq);

        assertThat(page).isSameAs(fakePage);
        verify(logRepo).findAll(pageReq);
    }
}
