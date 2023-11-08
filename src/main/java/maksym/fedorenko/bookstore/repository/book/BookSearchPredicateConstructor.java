package maksym.fedorenko.bookstore.repository.book;

import static maksym.fedorenko.bookstore.model.QBook.book;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import maksym.fedorenko.bookstore.dto.book.BookSearchParametersDto;
import org.springframework.stereotype.Component;

@Component
public class BookSearchPredicateConstructor {
    public Predicate construct(BookSearchParametersDto searchDto) {
        BooleanExpression predicate = book.isNotNull();
        if (searchDto.title() != null) {
            predicate = predicate.and(book.title.containsIgnoreCase(searchDto.title()));
        }
        if (searchDto.author() != null && searchDto.author().length > 0) {
            predicate = predicate.and(book.author.in(searchDto.author()));
        }
        if (searchDto.minPrice() != null) {
            predicate = predicate.and(book.price.goe(searchDto.minPrice()));
        }
        if (searchDto.maxPrice() != null) {
            predicate = predicate.and(book.price.loe(searchDto.maxPrice()));
        }
        return predicate;
    }
}
