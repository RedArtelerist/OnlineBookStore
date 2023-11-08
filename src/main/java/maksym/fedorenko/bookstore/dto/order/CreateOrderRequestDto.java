package maksym.fedorenko.bookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateOrderRequestDto(
        @NotNull @Length(max = 128) String shippingAddress) {
}
