package maksym.fedorenko.bookstore.mapper;

import java.math.BigDecimal;
import maksym.fedorenko.bookstore.dto.order.CreateOrderRequestDto;
import maksym.fedorenko.bookstore.dto.order.OrderDto;
import maksym.fedorenko.bookstore.model.Order;
import maksym.fedorenko.bookstore.model.OrderItem;
import maksym.fedorenko.bookstore.model.ShoppingCart;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = OrderItemMapper.class
)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", source = "cart.cartItems")
    Order toOrder(ShoppingCart cart, CreateOrderRequestDto requestDto);

    @AfterMapping
    default void setOrderToItems(@MappingTarget Order order) {
        order.getOrderItems()
                .forEach(item -> item.setOrder(order));
    }

    @AfterMapping
    default void setTotalPrice(@MappingTarget Order order) {
        order.setTotal(order.getOrderItems().stream()
                .map(this::getTotalItemPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    private BigDecimal getTotalItemPrice(OrderItem orderItem) {
        BigDecimal quantity = BigDecimal.valueOf(orderItem.getQuantity());
        return orderItem.getPrice().multiply(quantity);
    }
}
