package maksym.fedorenko.bookstore.repository.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.exception.BookNotFoundException;
import maksym.fedorenko.bookstore.exception.BookNotSavedException;
import maksym.fedorenko.bookstore.model.Book;
import maksym.fedorenko.bookstore.repository.BookRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;

    @Override
    public Book save(Book book) {
        try {
            sessionFactory.inTransaction(session -> session.persist(book));
            return book;
        } catch (Exception e) {
            throw new BookNotSavedException("Can't create new book", e);
        }
    }

    @Override
    public List<Book> findAll() {
        try {
            return sessionFactory.fromSession(
                    session -> session.createQuery("from Book", Book.class).getResultList()
            );
        } catch (Exception e) {
            throw new BookNotFoundException("Can't load list of books", e);
        }
    }
}
