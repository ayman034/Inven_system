package Inventory.System.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoomRequest {

    @NotBlank(message = "Jina la room linahitajika")
    private String name;
}
