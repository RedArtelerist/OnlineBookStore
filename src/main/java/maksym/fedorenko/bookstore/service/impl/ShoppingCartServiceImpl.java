package maksym.fedorenko.bookstore.service.impl;

import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.shoppingCart.CartDto;
import maksym.fedorenko.bookstore.dto.shoppingCart.CartItemDto;
import maksym.fedorenko.bookstore.dto.shoppingCart.CreateCartItemRequestDto;
import maksym.fedorenko.bookstore.dto.shoppingCart.UpdateCartItemRequestDto;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.model.ShoppingCart;
import maksym.fedorenko.bookstore.repository.CartItemRepository;
import maksym.fedorenko.bookstore.repository.ShoppingCartRepository;
import maksym.fedorenko.bookstore.service.ShoppingCartService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartDto getUserCart(Authentication authentication) {
        String username = authentication.getName();
        ShoppingCart cart = shoppingCartRepository.findByUserEmail(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find cart for user with email=%s".formatted(username))
                );
        return null;
    }

    @Override
    public CartItemDto addCartItem(CreateCartItemRequestDto requestDto) {
        return null;
    }

    @Override
    public CartItemDto updateCartItem(UpdateCartItemRequestDto requestDto) {
        return null;
    }

    @Override
    public void deleteCartItem(Long cartItemId) {

    }
}
