package maksym.fedorenko.bookstore.repository;

import java.util.Optional;
import maksym.fedorenko.bookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("""
        from ShoppingCart c
        join fetch c.cartItems ci
        join fetch ci.book
        where c.user.email = :email""")
    Optional<ShoppingCart> findByUserEmail(String email);
}
