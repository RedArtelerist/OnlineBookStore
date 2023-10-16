package maksym.fedorenko.bookstore.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import maksym.fedorenko.bookstore.model.Book;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateBookRequestDto {
    @Length(max = 128)
    private String title;

    @Length(max = 128)
    private String author;

    @Length(max = 128)
    private String isbn;

    @Positive
    private BigDecimal price;

    @Length(max = 1024)
    private String description;

    @Length(max = 128)
    private String coverImage;

    public void updateBook(Book book) {
        if (title != null && !title.isBlank()) {
            book.setTitle(title);
        }
        if (author != null && !author.isBlank()) {
            book.setAuthor(author);
        }
        if (isbn != null && isbn.isBlank()) {
            book.setIsbn(isbn);
        }
        if (price != null) {
            book.setPrice(price);
        }
        if (description != null) {
            book.setDescription(description);
        }
        if (coverImage != null) {
            book.setCoverImage(coverImage);
        }
    }
}
