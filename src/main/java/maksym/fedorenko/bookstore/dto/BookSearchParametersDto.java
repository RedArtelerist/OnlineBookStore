package maksym.fedorenko.bookstore.dto;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.math.BigDecimal;
import maksym.fedorenko.bookstore.model.QBook;

public record BookSearchParametersDto(
        String title, String[] author, BigDecimal minPrice, BigDecimal maxPrice
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
