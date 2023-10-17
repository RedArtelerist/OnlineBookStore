package maksym.fedorenko.bookstore.dto;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import maksym.fedorenko.bookstore.model.QBook;
import org.hibernate.validator.constraints.Length;

public record BookSearchParametersDto(
        @Length(min = 3, max = 128) String title,
        String[] author,
        @Positive BigDecimal minPrice,
        @Positive BigDecimal maxPrice
) {
    public Predicate getFilterPredicate(QBook book) {
        BooleanExpression predicate = book.isNotNull();
        if (title != null) {
            predicate = predicate.and(book.title.containsIgnoreCase(title));
        }
        if (author != null && author.length > 0) {
            predicate = predicate.and(book.author.in(author));
        }
        if (minPrice != null) {
            predicate = predicate.and(book.price.goe(minPrice));
        }
        if (maxPrice != null) {
            predicate = predicate.and(book.price.loe(maxPrice));
        }
        return predicate;
    }
}
