package Inventory.System.dto;

import Inventory.System.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Jina kamili linahitajika")
    private String fullName;

    @NotBlank(message = "Username inahitajika")
    private String username;

    @NotBlank(message = "Password inahitajika")
    @Size(min = 8, message = "Password lazima iwe na herufi angalau 8")
    private String password;

    @NotNull(message = "Role inahitajika")
    private Role role;
}
