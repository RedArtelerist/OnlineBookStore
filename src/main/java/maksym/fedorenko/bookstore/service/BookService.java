package maksym.fedorenko.bookstore.service;

import java.util.List;
import maksym.fedorenko.bookstore.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
