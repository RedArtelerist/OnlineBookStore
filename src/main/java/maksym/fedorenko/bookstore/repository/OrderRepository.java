package maksym.fedorenko.bookstore.repository;

import java.util.Optional;
import maksym.fedorenko.bookstore.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findById(Long id);

    @EntityGraph(attributePaths = "orderItems")
    Page<Order> findAllByUserEmail(String email, Pageable pageable);

    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findByIdAndUserEmail(Long id, String email);
}
