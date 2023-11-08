package maksym.fedorenko.bookstore.service;

import maksym.fedorenko.bookstore.dto.shoppingcart.CartDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    CartDto getUserCart(Authentication authentication);

    CartDto addCartItem(Authentication authentication, CreateCartItemRequestDto requestDto);

    CartDto updateCartItem(
            Authentication authentication, Long id, UpdateCartItemRequestDto requestDto
    );

    CartDto deleteCartItem(Authentication authentication, Long id);
}
