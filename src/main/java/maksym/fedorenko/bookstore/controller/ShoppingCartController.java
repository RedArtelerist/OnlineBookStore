package maksym.fedorenko.bookstore.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@Validated
public class ShoppingCartController {
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Object getUserCart(Authentication authentication) {
        return null;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Object addCartItem(Authentication authentication) {
        return null;
    }

    @PutMapping("/cart-items/{cartItemId}")
    public Object updateCartItem(
            Authentication authentication,
            @Positive @PathVariable Long cartItemId) {
        return null;
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItem(
            Authentication authentication,
            @Positive @PathVariable Long cartItemId) {

    }
}
