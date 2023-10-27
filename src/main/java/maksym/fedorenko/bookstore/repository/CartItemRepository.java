package maksym.fedorenko.bookstore.repository;

import maksym.fedorenko.bookstore.model.CartItem;
import maksym.fedorenko.bookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = "book")
    CartItem findByShoppingCartAndBookId(ShoppingCart cart, Long bookId);
}
