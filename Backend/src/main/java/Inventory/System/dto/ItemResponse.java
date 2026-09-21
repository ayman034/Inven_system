package Inventory.System.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse {
    private Long id;
    private String name;
    private String category;
    private Integer quantity;   // Total registered quantity
    private Integer allocated;  // Quantity allocated across all rooms
    private Integer remaining;  // iliyobaki (quantity - allocated)
}
