package maksym.fedorenko.bookstore.mapper;

import maksym.fedorenko.bookstore.config.MapperConfig;
import maksym.fedorenko.bookstore.dto.order.OrderItemDto;
import maksym.fedorenko.bookstore.model.CartItem;
import maksym.fedorenko.bookstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(target = "price", source = "book.price")
    @Mapping(target = "id", ignore = true)
    OrderItem toOrderItem(CartItem cartItem);
}
