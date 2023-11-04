package maksym.fedorenko.bookstore.dto.book;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.Length;

public record BookSearchParametersDto(
        @Length(max = 128) String title,
        String[] author,
        @Positive BigDecimal minPrice,
        @Positive BigDecimal maxPrice
) {
}
