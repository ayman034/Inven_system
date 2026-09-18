package Inventory.System.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {

    @NotNull(message = "Item inahitajika")
    private Long itemId;

    @NotNull(message = "Room inahitajika")
    private Long roomId;

    @NotNull(message = "Quantity inahitajika")
    @Min(value = 1, message = "Quantity lazima iwe zaidi ya 0")
    private Integer quantity;
}