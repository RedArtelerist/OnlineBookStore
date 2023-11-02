package maksym.fedorenko.bookstore.service;

import java.util.List;
import maksym.fedorenko.bookstore.dto.order.CreateOrderRequestDto;
import maksym.fedorenko.bookstore.dto.order.OrderDto;
import maksym.fedorenko.bookstore.dto.order.OrderItemDto;
import maksym.fedorenko.bookstore.dto.order.UpdateOrderRequestDto;
import org.springframework.security.core.Authentication;

public interface OrderService {
    OrderDto createOrder(Authentication authentication, CreateOrderRequestDto requestDto);

    List<OrderDto> findAll(Authentication authentication);

    OrderDto getById(Authentication authentication, Long id);

    OrderItemDto getOrderItemById(Authentication authentication, Long id);

    OrderDto update(Long id, UpdateOrderRequestDto requestDto);
}
