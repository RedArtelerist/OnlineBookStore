package maksym.fedorenko.bookstore.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import maksym.fedorenko.bookstore.model.Status;

public record OrderDto(
        Long id,
        Long userId,
        List<OrderItemDto> orderItems,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime orderDate,
        BigDecimal total,
        Status status) {
}
