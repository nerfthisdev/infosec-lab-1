package yasheri.ru.infoseclab1.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must contain 3–50 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9_.-]+$",
                message = "Username contains invalid characters"
        )
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must contain 8–72 characters")
        String password
) {
}
