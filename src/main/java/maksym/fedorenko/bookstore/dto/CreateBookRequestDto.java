package maksym.fedorenko.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.Length;

public record CreateBookRequestDto(
        @NotNull @Length(max = 128) String title,
        @NotNull @Length(max = 128) String author,
        @NotNull @Length(max = 128) String isbn,
        @NotNull @Positive BigDecimal price,
        @Length(max = 1024) String description,
        @Length(max = 128) String coverImage) {
}
