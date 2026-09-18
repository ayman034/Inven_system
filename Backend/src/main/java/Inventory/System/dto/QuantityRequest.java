package Inventory.System.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuantityRequest {
    @NotNull(message = "Quantity inahitajika")
    @Min(value = 1, message = "Quantity lazima iwe zaidi ya 0")
    private Integer quantity;
}
