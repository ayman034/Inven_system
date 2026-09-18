package Inventory.System.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username inahitajika")
    private String username;

    @NotBlank(message = "Password inahitajika")
    private String password;
}
