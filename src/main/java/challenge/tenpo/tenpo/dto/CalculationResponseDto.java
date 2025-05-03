package challenge.tenpo.tenpo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculationResponseDto {
    private double result;
    public CalculationResponseDto(double result) {
        this.result = result;
    }

}
