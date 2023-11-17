package maksym.fedorenko.bookstore.mapper;

import java.math.BigDecimal;
import java.util.List;
import maksym.fedorenko.bookstore.config.MapperConfig;
import maksym.fedorenko.bookstore.dto.order.CreateOrderRequestDto;
import maksym.fedorenko.bookstore.dto.order.OrderDto;
import maksym.fedorenko.bookstore.model.CartItem;
import maksym.fedorenko.bookstore.model.Order;
import maksym.fedorenko.bookstore.model.ShoppingCart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", source = "cart.cartItems")
    @Mapping(target = "total", source = "cart.cartItems", qualifiedByName = "totalPrice")
    Order toOrder(ShoppingCart cart, CreateOrderRequestDto requestDto);

    @AfterMapping
    default void setOrderToItems(@MappingTarget Order order) {
        order.getOrderItems()
                .forEach(item -> item.setOrder(order));
    }

    @Named("totalPrice")
    default BigDecimal totalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::getTotalItemPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalItemPrice(CartItem cartItem) {
        BigDecimal quantity = BigDecimal.valueOf(cartItem.getQuantity());
        return cartItem.getBook().getPrice().multiply(quantity);
    }
}
