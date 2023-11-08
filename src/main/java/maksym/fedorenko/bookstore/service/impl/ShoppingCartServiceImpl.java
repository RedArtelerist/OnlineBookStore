package maksym.fedorenko.bookstore.service.impl;

import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.mapper.CartItemMapper;
import maksym.fedorenko.bookstore.mapper.ShoppingCartMapper;
import maksym.fedorenko.bookstore.model.CartItem;
import maksym.fedorenko.bookstore.model.ShoppingCart;
import maksym.fedorenko.bookstore.model.User;
import maksym.fedorenko.bookstore.repository.BookRepository;
import maksym.fedorenko.bookstore.repository.CartItemRepository;
import maksym.fedorenko.bookstore.repository.ShoppingCartRepository;
import maksym.fedorenko.bookstore.repository.UserRepository;
import maksym.fedorenko.bookstore.service.ShoppingCartService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartDto getUserCart(Authentication authentication) {
        ShoppingCart cart = getUserShoppingCart(authentication.getName());
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartDto addCartItem(
            Authentication authentication, CreateCartItemRequestDto requestDto) {
        checkIfBookExists(requestDto.bookId());
        ShoppingCart cart = shoppingCartRepository
                .findByUserEmail(authentication.getName())
                .orElseGet(() -> createShoppingCartForUser((User) authentication.getPrincipal()));
        cartItemRepository.findByShoppingCartAndBookId(cart, requestDto.bookId())
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + requestDto.quantity()),
                        () -> createCartItem(requestDto, cart)
                );
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartDto updateCartItem(
            Authentication authentication, Long id, UpdateCartItemRequestDto requestDto) {
        ShoppingCart cart = getUserShoppingCart(authentication.getName());
        CartItem cartItem = findCartItemByIdAndUser(authentication.getName(), id);
        cartItem.setQuantity(requestDto.quantity());
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartDto deleteCartItem(Authentication authentication, Long id) {
        ShoppingCart cart = getUserShoppingCart(authentication.getName());
        CartItem cartItem = findCartItemByIdAndUser(authentication.getName(), id);
        cart.removeCartItem(cartItem);
        return shoppingCartMapper.toDto(cart);
    }

    private ShoppingCart getUserShoppingCart(String email) {
        return shoppingCartRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find cart for user with email: %s".formatted(email)
                ));
    }

    private ShoppingCart createShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(userRepository.getReferenceById(user.getId()));
        return shoppingCartRepository.save(shoppingCart);
    }

    private void createCartItem(CreateCartItemRequestDto requestDto, ShoppingCart cart) {
        CartItem cartItem = cartItemMapper.toCartItem(requestDto);
        cart.addCartItem(cartItem);
        cartItem.setBook(bookRepository.getReferenceById(requestDto.bookId()));
        cartItemRepository.save(cartItem);
    }

    private CartItem findCartItemByIdAndUser(String email, Long id) {
        return cartItemRepository.findByIdAndShoppingCartUserEmail(id, email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find cart item with id=%d in user's cart with email: %s"
                                .formatted(id, email))
                );
    }

    private void checkIfBookExists(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException("Can't find book by id: %d".formatted(bookId));
        }
    }
}
