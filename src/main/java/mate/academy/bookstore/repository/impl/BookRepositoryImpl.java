package mate.academy.bookstore.repository.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.exception.BookNotFoundException;
import mate.academy.bookstore.exception.BookNotSavedException;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.BookRepository;
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
