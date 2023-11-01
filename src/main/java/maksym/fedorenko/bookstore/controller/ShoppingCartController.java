package maksym.fedorenko.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CartItemDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import maksym.fedorenko.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import maksym.fedorenko.bookstore.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@Validated
@RequiredArgsConstructor
@Tag(name = "Shopping cart management", description = "Endpoints for working with cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(
            summary = "Get user cart",
            description = "Retrieving all items that are contained in the user cart."
    )
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public CartDto getUserCart(Authentication authentication) {
        return shoppingCartService.getUserCart(authentication);
    }

    @Operation(
            summary = "Add book to user cart",
            description = "Add a specific book and its quantity to the cart."
    )
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public CartItemDto addCartItem(
            Authentication authentication,
            @Valid @RequestBody CreateCartItemRequestDto requestDto) {
        return shoppingCartService.addCartItem(authentication, requestDto);
    }

    @Operation(
            summary = "Update cart item",
            description = "Update the quantity of a specific item in the cart."
    )
    @PutMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    public CartItemDto updateCartItem(
            @Positive @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequestDto requestDto) {
        return shoppingCartService.updateCartItem(cartItemId, requestDto);
    }

    @Operation(
            summary = "Delete item from cart",
            description = "Delete specific item from user cart."
    )
    @DeleteMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItem(@Positive @PathVariable Long cartItemId) {
        shoppingCartService.deleteCartItem(cartItemId);
    }
}
