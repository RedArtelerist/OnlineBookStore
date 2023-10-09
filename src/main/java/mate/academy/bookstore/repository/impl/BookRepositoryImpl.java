package mate.academy.bookstore.repository.impl;

import java.util.List;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.BookRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public BookRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Book save(Book book) {
        try {
            sessionFactory.inTransaction(session -> session.persist(book));
            return book;
        } catch (Exception e) {
            throw new RuntimeException("Can't create new book", e);
        }
    }

    @Override
    public List<Book> findAll() {
        try {
            return sessionFactory.fromSession(
                    session -> session.createQuery("from Book", Book.class).getResultList()
            );
        } catch (Exception e) {
            throw new RuntimeException("Can't load list of books", e);
        }
    }
}
