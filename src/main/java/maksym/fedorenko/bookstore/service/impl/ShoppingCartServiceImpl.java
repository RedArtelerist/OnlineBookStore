package maksym.fedorenko.bookstore.service.impl;

import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartItemDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.mapper.CartItemMapper;
import maksym.fedorenko.bookstore.mapper.ShoppingCartMapper;
import maksym.fedorenko.bookstore.model.Book;
import maksym.fedorenko.bookstore.model.CartItem;
import maksym.fedorenko.bookstore.model.ShoppingCart;
import maksym.fedorenko.bookstore.repository.BookRepository;
import maksym.fedorenko.bookstore.repository.CartItemRepository;
import maksym.fedorenko.bookstore.repository.ShoppingCartRepository;
import maksym.fedorenko.bookstore.service.ShoppingCartService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartDto getUserCart(Authentication authentication) {
        String username = authentication.getName();
        ShoppingCart cart = shoppingCartRepository.findByUserEmailWithCartItems(username)
                .orElseThrow(() -> getNotFoundCartException(username));
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public CartItemDto addCartItem(
            Authentication authentication,
            CreateCartItemRequestDto requestDto) {
        ShoppingCart cart = retrieveUserCartFromDb(authentication.getName());
        CartItem cartItem = cartItemRepository.findByShoppingCartAndBookId(
                cart, requestDto.bookId());
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + requestDto.quantity());
        } else {
            cartItem = cartItemMapper.toCartItem(requestDto);
            cartItem.setShoppingCart(cart);
            cartItem.setBook(getBookFromCartItem(requestDto));
        }
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemDto updateCartItem(Long id, UpdateCartItemRequestDto requestDto) {
        CartItem cartItem = findCartItemById(id);
        cartItem.setQuantity(requestDto.quantity());
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void deleteCartItem(Long id) {
        cartItemRepository.delete(findCartItemById(id));
    }

    private CartItem findCartItemById(Long id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find cart item by id: %d".formatted(id))
                );
    }

    private Book getBookFromCartItem(CreateCartItemRequestDto requestDto) {
        Long bookId = requestDto.bookId();
        return bookRepository.findById(requestDto.bookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find book by id: %d".formatted(bookId))
                );
    }

    private ShoppingCart retrieveUserCartFromDb(String username) {
        return shoppingCartRepository.findByUserEmail(username)
                .orElseThrow(() -> getNotFoundCartException(username));
    }

    private EntityNotFoundException getNotFoundCartException(String username) {
        return new EntityNotFoundException(
                "Can't find cart for user with email: %s".formatted(username)
        );
    }
}
