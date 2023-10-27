package maksym.fedorenko.bookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequestDto(
        @NotBlank
        @Size(max = 32)
        String name,
        @Size(max = 256)
        String description) {
}
