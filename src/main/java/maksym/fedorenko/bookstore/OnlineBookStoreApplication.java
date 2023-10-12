package maksym.fedorenko.bookstore;

import java.math.BigDecimal;
import maksym.fedorenko.bookstore.model.Book;
import maksym.fedorenko.bookstore.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(BookService bookService) {
        return args -> {
            Book book = new Book();
            book.setTitle("Effective Java");
            book.setAuthor("Bloch, Joshua");
            book.setIsbn("147814941");
            book.setPrice(BigDecimal.valueOf(800));
            bookService.save(book);
            bookService.findAll().forEach(System.out::println);
        };
    }
}
