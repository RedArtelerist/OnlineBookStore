package maksym.fedorenko.bookstore.repository;

import java.util.Optional;
import maksym.fedorenko.bookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUserEmail(String email);

    @Query("""
        from ShoppingCart c
        left join fetch c.user u
        left join fetch c.cartItems ci
        left join fetch ci.book
        where u.email = :email""")
    Optional<ShoppingCart> findByUserEmailWithCartItems(String email);
}
