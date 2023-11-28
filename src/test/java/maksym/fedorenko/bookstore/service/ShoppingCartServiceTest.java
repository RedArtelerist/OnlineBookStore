package maksym.fedorenko.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartItemDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.mapper.CartItemMapper;
import maksym.fedorenko.bookstore.mapper.CartItemMapperImpl;
import maksym.fedorenko.bookstore.mapper.ShoppingCartMapper;
import maksym.fedorenko.bookstore.mapper.ShoppingCartMapperImpl;
import maksym.fedorenko.bookstore.model.Book;
import maksym.fedorenko.bookstore.model.CartItem;
import maksym.fedorenko.bookstore.model.ShoppingCart;
import maksym.fedorenko.bookstore.repository.BookRepository;
import maksym.fedorenko.bookstore.repository.CartItemRepository;
import maksym.fedorenko.bookstore.repository.ShoppingCartRepository;
import maksym.fedorenko.bookstore.repository.UserRepository;
import maksym.fedorenko.bookstore.service.impl.ShoppingCartServiceImpl;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private CartItemMapper cartItemMapper = new CartItemMapperImpl();
    @Spy
    private ShoppingCartMapper shoppingCartMapper = new ShoppingCartMapperImpl(cartItemMapper);
    @Mock
    private Authentication authentication;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Get user cart with valid authentication")
    void getUserCart_ValidAuthentication_ShouldReturnCartDto() {
        String username = "user@gmail.com";
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);

        when(authentication.getName()).thenReturn(username);
        when(shoppingCartRepository.findByUserEmail(username)).thenReturn(Optional.of(cart));

        CartDto actual = shoppingCartService.getUserCart(authentication);
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", 1L)
                .extracting(CartDto::cartItems)
                .asInstanceOf(InstanceOfAssertFactories.list(CartItemDto.class))
                .hasSize(0);
    }

    @Test
    @DisplayName("Get user cart with invalid authentication")
    void getUserCart_InvalidAuthentication_ShouldThrowException() {
        String username = "user@gmail.com";

        when(authentication.getName()).thenReturn(username);
        when(shoppingCartRepository.findByUserEmail(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shoppingCartService.getUserCart(authentication))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Can't find cart for user with email: %s".formatted(username));
    }

    @Test
    @DisplayName("Add valid item to cart")
    void addCartItem_ValidRequestDto_ShouldReturnCartDto() {
        final String username = "user@gmail.com";
        final CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(1L, 2);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test");

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(cart);
        cartItem.setBook(book);
        cartItem.setQuantity(2);

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        when(shoppingCartRepository.findByUserEmail(username)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByShoppingCartAndBookId(cart, 1L)).thenReturn(Optional.empty());
        when(bookRepository.getReferenceById(1L)).thenReturn(book);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        CartDto actual = shoppingCartService.addCartItem(authentication, requestDto);

        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", 1L)
                .extracting(CartDto::cartItems)
                .asInstanceOf(InstanceOfAssertFactories.list(CartItemDto.class))
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("bookId", 1L)
                .hasFieldOrPropertyWithValue("bookTitle", "Test")
                .hasFieldOrPropertyWithValue("quantity", 2);
    }

    @Test
    @DisplayName("Add valid item that already exist in cart")
    void addCartItem_ValidRequestDtoWithExistentItemInCart_ShouldReturnCartDto() {
        final String username = "user@gmail.com";
        final CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(1L, 2);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test");

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setShoppingCart(cart);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        cart.addCartItem(cartItem);

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        when(shoppingCartRepository.findByUserEmail(username)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByShoppingCartAndBookId(cart, 1L))
                .thenReturn(Optional.of(cartItem));

        CartDto actual = shoppingCartService.addCartItem(authentication, requestDto);

        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", 1L)
                .extracting(CartDto::cartItems)
                .asInstanceOf(InstanceOfAssertFactories.list(CartItemDto.class))
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bookId", 1L)
                .hasFieldOrPropertyWithValue("bookTitle", "Test")
                .hasFieldOrPropertyWithValue("quantity", 4);
    }

    @Test
    @DisplayName("Add invalid item with non-existent book")
    void addCartItem_InvalidRequestDtoWithNonExistentBook_ShouldThrowException() {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(1L, 2);

        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> shoppingCartService.addCartItem(authentication, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Can't find book by id: %d".formatted(1L));
    }

    @Test
    @DisplayName("Update existing cart item by id")
    void updateCartItem_ValidRequestDto_ShouldReturnCartDto() {
        final Long id = 1L;
        final String username = "user@gmail.com";
        final UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(2);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test");

        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setShoppingCart(cart);
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        cart.addCartItem(cartItem);

        when(authentication.getName()).thenReturn(username);
        when(shoppingCartRepository.findByUserEmail(username)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByIdAndShoppingCartUserEmail(id, username))
                .thenReturn(Optional.of(cartItem));

        CartDto actual = shoppingCartService.updateCartItem(authentication, id, requestDto);

        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", 1L)
                .extracting(CartDto::cartItems)
                .asInstanceOf(InstanceOfAssertFactories.list(CartItemDto.class))
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bookId", 1L)
                .hasFieldOrPropertyWithValue("bookTitle", "Test")
                .hasFieldOrPropertyWithValue("quantity", 2);
    }

    @Test
    @DisplayName("Delete existing cart item by id")
    void deleteCartItem_ValidRequestDto_ShouldReturnCartDto() {
        final Long id = 1L;
        final String username = "user@gmail.com";

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test");

        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setShoppingCart(cart);
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        cart.addCartItem(cartItem);

        when(authentication.getName()).thenReturn(username);
        when(shoppingCartRepository.findByUserEmail(username)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByIdAndShoppingCartUserEmail(1L, username))
                .thenReturn(Optional.of(cartItem));

        CartDto actual = shoppingCartService.deleteCartItem(authentication, id);

        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", 1L)
                .extracting(CartDto::cartItems)
                .asInstanceOf(InstanceOfAssertFactories.list(CartItemDto.class))
                .hasSize(0);
    }

    @Test
    @DisplayName("Delete cart item by non-existent id")
    void deleteCartItem_WithNonExistentId_ShouldThrowException() {
        final Long id = 1L;
        String username = "user@gmail.com";
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);

        when(authentication.getName()).thenReturn(username);
        when(shoppingCartRepository.findByUserEmail(username)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByIdAndShoppingCartUserEmail(id, username))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> shoppingCartService.deleteCartItem(authentication, id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Can't find cart item with id=%d in user's cart with email: %s"
                        .formatted(id, username));
    }
}
