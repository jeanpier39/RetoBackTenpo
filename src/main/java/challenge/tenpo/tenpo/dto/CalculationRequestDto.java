package challenge.tenpo.tenpo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculationRequestDto {
    private double num1;
    private double num2;

    public CalculationRequestDto() {
    }
    public CalculationRequestDto(double num1, double num2) {
        this.num1 = num1;
        this.num2 = num2;
    }

}
