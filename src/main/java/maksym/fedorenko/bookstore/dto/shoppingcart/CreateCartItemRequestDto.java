package maksym.fedorenko.bookstore.dto.shoppingcart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Range;

public record CreateCartItemRequestDto(
        @NotNull @Positive
        Long bookId,
        @NotNull @Range(min = 1, max = 10)
        Integer quantity) {
}
