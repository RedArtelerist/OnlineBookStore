package maksym.fedorenko.bookstore.repository;

import java.util.Optional;
import maksym.fedorenko.bookstore.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findById(Long id);
}
