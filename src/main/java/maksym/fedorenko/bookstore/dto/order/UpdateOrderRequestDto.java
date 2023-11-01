package maksym.fedorenko.bookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import maksym.fedorenko.bookstore.model.Status;

public record UpdateOrderRequestDto(@NotNull Status status) {
}
