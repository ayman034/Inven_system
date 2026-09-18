package Inventory.System.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRequest {

    @NotBlank(message = "Jina la item linahitajika")
    private String name;

    @NotBlank(message = "Category inahitajika")
    private String category;

    @NotNull(message = "Quantity inahitajika")
    @Min(value = 0, message = "Quantity haiwezi kuwa hasi")
    private Integer quantity;
}
