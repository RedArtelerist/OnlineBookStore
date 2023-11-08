package maksym.fedorenko.bookstore.dto.order;

import static maksym.fedorenko.bookstore.model.Order.Status;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        Long userId,
        List<OrderItemDto> orderItems,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime orderDate,
        BigDecimal total,
        Status status) {
}
