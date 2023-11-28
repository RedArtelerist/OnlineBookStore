package maksym.fedorenko.bookstore.repository;

import static maksym.fedorenko.bookstore.model.QBook.book;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.querydsl.core.types.Predicate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import maksym.fedorenko.bookstore.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/categories/add-default-categories.sql",
        "classpath:database/books/add-default-books.sql"
})
@Sql(scripts = {"classpath:database/books/delete-all-books.sql",
        "classpath:database/categories/delete-all-categories.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find book by nonexistent id")
    void findById_NonExistentId_ReturnEmptyOptional() {
        Optional<Book> actual = bookRepository.findById(10L);
        assertEquals(Optional.empty(), actual);
    }

    @Test
    @DisplayName("Find books by nonexistent category id")
    void findAllByCategoriesId_NonExistentCategoryId_ReturnEmptyList() {
        Long categoryId = 3L;
        Page<Book> actual = bookRepository.findAllByCategoriesId(
                categoryId, PageRequest.of(0, 10)
        );
        assertEquals(0, actual.getTotalElements());
    }

    @Test
    @DisplayName("Find books by existent category id")
    void findAllByCategoriesId_ExistentCategory_ReturnNonEmptyList() {
        Long categoryId = 1L;
        Page<Book> actual = bookRepository.findAllByCategoriesId(
                categoryId, PageRequest.of(0, 10)
        );
        assertEquals(3, actual.getTotalElements());
        assertEquals(List.of(1L, 2L, 5L), actual.stream().map(Book::getId).toList());
    }

    @Test
    @DisplayName("Search books by title and price range")
    void filterBooks_PriceBetween20And30AndTitleLikeJava_ReturnNonEmptyList() {
        Predicate filterPredicate = book.isNotNull()
                .and(book.price.between(BigDecimal.valueOf(20), BigDecimal.valueOf(30)))
                .and(book.title.containsIgnoreCase("java"));
        Page<Book> actual = bookRepository.findAll(
                filterPredicate, PageRequest.of(0, 10)
        );
        assertEquals(1, actual.getTotalElements());
        assertEquals(3L, actual.getContent().get(0).getId());
    }
}
