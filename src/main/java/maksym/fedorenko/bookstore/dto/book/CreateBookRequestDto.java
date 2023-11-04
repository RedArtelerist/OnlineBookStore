package maksym.fedorenko.bookstore.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.validator.constraints.Length;

public record CreateBookRequestDto(
        @NotBlank @Length(max = 128) String title,
        @NotBlank @Length(max = 128) String author,
        @NotBlank @Length(max = 128)
        @Pattern(regexp = "^(?=(?:[^0-9]*[0-9]){10}(?:(?:[^0-9]*[0-9]){3})?$)[\\d-]+$",
                message = "must contain 10 and may or may not contain a hyphen")
        String isbn,
        @NotNull @Positive BigDecimal price,
        @Length(max = 1024) String description,
        @Length(max = 128) String coverImage,
        @NotNull List<@Positive Long> categoryIds) {
}
