package challenge.tenpo.tenpo.controller;

import challenge.tenpo.tenpo.dto.CalculationRequestDto;
import challenge.tenpo.tenpo.dto.CalculationResponseDto;
import challenge.tenpo.tenpo.dto.ErrorDto;
import challenge.tenpo.tenpo.entity.LogEntry;
import challenge.tenpo.tenpo.service.CalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "calculate porcentaje", description = "Calculo con porcentaje dinamico")
public class ApiController {
    private final CalculationService calcService;

    public ApiController(CalculationService calcService) {
        this.calcService = calcService;
    }

    @Operation(summary = "Suma num1 + num2 y aplica un porcentaje mockeado")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cálculo correcto",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CalculationResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se pudo realizar el cálculo",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            )
    })
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
