package maksym.fedorenko.bookstore.repository;

import java.util.Optional;
import maksym.fedorenko.bookstore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("from OrderItem i where i.id = ?1 and i.order.id = ?2 and i.order.user.email = ?3")
    Optional<OrderItem> findByIdAndOrderAndUser(Long id, Long orderId, String email);
}
