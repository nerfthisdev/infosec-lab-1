package yasheri.ru.infoseclab1.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDataRequest(
        @NotBlank(message = "Text is required")
        @Size(max = 1000, message = "Text must contain at most 1000 characters")
        String text
) {
}
