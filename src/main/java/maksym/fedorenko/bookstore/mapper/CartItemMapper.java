package maksym.fedorenko.bookstore.mapper;

import maksym.fedorenko.bookstore.config.MapperConfig;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartItemDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import maksym.fedorenko.bookstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);

    CartItem toCartItem(CreateCartItemRequestDto requestDto);
}
