package maksym.fedorenko.bookstore.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequestDto(
        @NotNull
        @Size(max = 32)
        String name,
        @Size(max = 256)
        String description) {
}
