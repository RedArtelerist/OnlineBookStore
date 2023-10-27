package maksym.fedorenko.bookstore.repository;

import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.Optional;
import maksym.fedorenko.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, QuerydslPredicateExecutor<Book> {
    @EntityGraph(attributePaths = "categories")
    Optional<Book> findById(Long id);

    @EntityGraph(attributePaths = "categories")
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "categories")
    Page<Book> findAll(Predicate predicate, Pageable pageable);

    List<Book> findAllByCategoriesId(Long id, Pageable pageable);
}
