package challenge.tenpo.tenpo.service;

import challenge.tenpo.tenpo.entity.LogEntry;
import challenge.tenpo.tenpo.repository.LogEntryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CalculationService {
    private final PercentageService percentageService;
    private final LogEntryRepository logRepo;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public CalculationService(PercentageService percentageService, LogEntryRepository logRepo) {
        this.percentageService = percentageService;
        this.logRepo = logRepo;
    }

    public double calculate(double num1, double num2) {
        double result = 0;
        String error = null;
        try {
            double sum = num1 + num2;
            double pct = percentageService.getPercentage(); // usa cache
            result = sum + (sum * pct / 100);
        } catch (RuntimeException e) {
            result = 0;
            error = e.getMessage();
            throw e;
        } finally {
            double finalResult = result;
            String finalError = error;
            executor.submit(() ->
                    saveLog("/api/calculate",
                            "num1=" + num1 + "&num2=" + num2,
                            String.valueOf(finalResult),
                            finalError));
        }
        return result;
    }

    private void saveLog(String endpoint, String params, String response, String error) {
        LogEntry entry = new LogEntry();
        entry.setTimestamp(LocalDateTime.now());
        entry.setEndpoint(endpoint);
        entry.setParameters(params);
        entry.setResponse(response);
        entry.setError(error);
        logRepo.save(entry);
    }

    public Page<LogEntry> getLogs(Pageable pageable) {
        return logRepo.findAll(pageable);
    }
}

