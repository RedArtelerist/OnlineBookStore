package maksym.fedorenko.bookstore.mapper;

import maksym.fedorenko.bookstore.dto.BookDto;
import maksym.fedorenko.bookstore.dto.CreateBookRequestDto;
import maksym.fedorenko.bookstore.dto.UpdateBookRequestDto;
import maksym.fedorenko.bookstore.model.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toBook(CreateBookRequestDto bookRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapUpdateRequestToBook(
            UpdateBookRequestDto updateBookRequestDto, @MappingTarget Book book
    );
}
