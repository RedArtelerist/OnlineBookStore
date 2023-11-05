package maksym.fedorenko.bookstore.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.order.CreateOrderRequestDto;
import maksym.fedorenko.bookstore.dto.order.OrderDto;
import maksym.fedorenko.bookstore.dto.order.OrderItemDto;
import maksym.fedorenko.bookstore.dto.order.UpdateOrderRequestDto;
import maksym.fedorenko.bookstore.exception.CreateOrderException;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.mapper.OrderItemMapper;
import maksym.fedorenko.bookstore.mapper.OrderMapper;
import maksym.fedorenko.bookstore.model.Order;
import maksym.fedorenko.bookstore.model.OrderItem;
import maksym.fedorenko.bookstore.model.ShoppingCart;
import maksym.fedorenko.bookstore.repository.OrderItemRepository;
import maksym.fedorenko.bookstore.repository.OrderRepository;
import maksym.fedorenko.bookstore.repository.ShoppingCartRepository;
import maksym.fedorenko.bookstore.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderDto createOrder(Authentication authentication, CreateOrderRequestDto requestDto) {
        ShoppingCart cart = shoppingCartRepository
                .findByUserEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't create order because cart doesn't exist")
                );
        if (cart.getCartItems().size() == 0) {
            throw new CreateOrderException("Can't create order from empty cart");
        }
        Order order = orderMapper.toOrder(cart);
        order.setShippingAddress(requestDto.shippingAddress());
        cart.clear();
        shoppingCartRepository.save(cart);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderDto> findAll(Authentication authentication, Pageable pageable) {
        return orderRepository.findAllByUserEmail(authentication.getName(), pageable)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto getById(Authentication authentication, Long id) {
        Order order = orderRepository.findByIdAndUserEmail(id, authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "You don't have an order with id=%d".formatted(id))
                );
        return orderMapper.toDto(order);
    }

    @Override
    public OrderItemDto getOrderItemById(Authentication authentication, Long orderId, Long id) {
        OrderItem orderItem = orderItemRepository
                .findByIdAndOrderAndUser(id, orderId, authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Item with id %d not found in order with id %d".formatted(id, orderId))
                );
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public OrderDto update(Long id, UpdateOrderRequestDto requestDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find order with id=%d".formatted(id))
                );
        order.setStatus(requestDto.status());
        return orderMapper.toDto(orderRepository.save(order));
    }
}
