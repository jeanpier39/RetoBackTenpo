package challenge.tenpo.tenpo.controller;

import challenge.tenpo.tenpo.dto.CalculationRequestDto;
import challenge.tenpo.tenpo.dto.CalculationResponseDto;
import challenge.tenpo.tenpo.entity.LogEntry;
import challenge.tenpo.tenpo.service.CalculationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final CalculationService calcService;

    public ApiController(CalculationService calcService) {
        this.calcService = calcService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<CalculationResponseDto> calculate(@RequestBody CalculationRequestDto request) {
        double result = calcService.calculate(request.getNum1(), request.getNum2());
        return ResponseEntity.ok(new CalculationResponseDto(result));
    }

    @GetMapping("/logs")
    public ResponseEntity<Page<LogEntry>> logs(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(calcService.getLogs(PageRequest.of(page, size)));
    }
}
