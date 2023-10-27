package maksym.fedorenko.bookstore.service;

import maksym.fedorenko.bookstore.dto.shoppingCart.CartDto;
import maksym.fedorenko.bookstore.dto.shoppingCart.CartItemDto;
import maksym.fedorenko.bookstore.dto.shoppingCart.CreateCartItemRequestDto;
import maksym.fedorenko.bookstore.dto.shoppingCart.UpdateCartItemRequestDto;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    CartDto getUserCart(Authentication authentication);

    CartItemDto addCartItem(CreateCartItemRequestDto requestDto);

    CartItemDto updateCartItem(UpdateCartItemRequestDto requestDto);

    void deleteCartItem(Long cartItemId);
}
