package maksym.fedorenko.bookstore.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import maksym.fedorenko.bookstore.model.Book;
import org.hibernate.validator.constraints.Length;

public record UpdateBookRequestDto(
        @Length(max = 128) String title,
        @Length(max = 128) String author,
        @Length(max = 128) String isbn,
        @Positive BigDecimal price,
        @Length(max = 1024) String description,
        @Length(max = 128) String coverImage
) {
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
