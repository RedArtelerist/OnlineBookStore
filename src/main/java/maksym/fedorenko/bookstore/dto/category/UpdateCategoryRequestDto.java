package maksym.fedorenko.bookstore.dto.category;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequestDto(
        @Size(min = 3, max = 32)
        String name,
        @Size(max = 256)
        String description) {
}
