package maksym.fedorenko.bookstore.dto.shoppingCart;

import java.util.List;

public record CartDto(Long id, List<CartItemDto> cartItems) {
}
