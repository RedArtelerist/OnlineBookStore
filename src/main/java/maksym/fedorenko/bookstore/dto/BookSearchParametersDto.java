package maksym.fedorenko.bookstore.dto;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.math.BigDecimal;
import lombok.Data;
import maksym.fedorenko.bookstore.model.QBook;

@Data
public class BookSearchParametersDto {
    private String title;

    private String[] author;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

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
