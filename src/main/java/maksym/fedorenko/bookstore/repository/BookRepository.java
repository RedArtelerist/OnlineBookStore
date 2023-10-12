package maksym.fedorenko.bookstore.repository;

import java.util.List;
import java.util.Optional;
import maksym.fedorenko.bookstore.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();

    Optional<Book> findById(Long id);
}
