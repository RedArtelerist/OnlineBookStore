package maksym.fedorenko.bookstore.service;

import maksym.fedorenko.bookstore.dto.shoppingcart.CartDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartItemDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    CartDto getUserCart(Authentication authentication);

    CartItemDto addCartItem(Authentication authentication, CreateCartItemRequestDto requestDto);

    CartItemDto updateCartItem(Long id, UpdateCartItemRequestDto requestDto);

    void deleteCartItem(Long id);
}
