package maksym.fedorenko.bookstore.dto.shoppingCart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequestDto(
        @NotNull @Positive
        Integer quantity) {
}
