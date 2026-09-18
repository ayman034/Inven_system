package Inventory.System.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {

    private Long id;

    private Long itemId;

    private String itemName;

    private String category;

    private Long roomId;

    private String roomName;

    private Integer quantity;
}