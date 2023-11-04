package maksym.fedorenko.bookstore.dto.shoppingcart;

import java.util.List;

public record CartDto(Long id, List<CartItemDto> cartItems) {
}
