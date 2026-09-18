package Inventory.System.dto;

import Inventory.System.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "Jina kamili linahitajika")
    private String fullName;

    @NotNull(message = "Role inahitajika")
    private Role role;

    // Hiari - ikijazwa, password itabadilishwa
    private String password;
}
