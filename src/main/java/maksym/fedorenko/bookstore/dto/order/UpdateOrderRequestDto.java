package maksym.fedorenko.bookstore.dto.order;

import static maksym.fedorenko.bookstore.model.Order.Status;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderRequestDto(@NotNull Status status) {
}
