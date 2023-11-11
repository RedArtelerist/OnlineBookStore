package maksym.fedorenko.bookstore.repository;

import static maksym.fedorenko.bookstore.model.QBook.book;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.querydsl.core.types.Predicate;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import maksym.fedorenko.bookstore.model.Book;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    @SneakyThrows
    static void beforeAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/add-default-categories.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-default-books.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @SneakyThrows
    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/delete-default-books.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/delete-default-categories.sql")
            );
        }
    }

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
