package maksym.fedorenko.bookstore.dto.shoppingcart;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record UpdateCartItemRequestDto(
        @NotNull @Range(min = 1, max = 10)
        Integer quantity) {
}
