package maksym.fedorenko.bookstore.dto;

import java.math.BigDecimal;

public record BookDto(
        Long id, String title, String author, String ibn,
        BigDecimal price, String description, String coverImage
) {
}
