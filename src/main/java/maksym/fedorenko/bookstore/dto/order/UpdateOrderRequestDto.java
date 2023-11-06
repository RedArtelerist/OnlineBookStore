package maksym.fedorenko.bookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import maksym.fedorenko.bookstore.model.Order;

public record UpdateOrderRequestDto(@NotNull Order.Status status) {
}
