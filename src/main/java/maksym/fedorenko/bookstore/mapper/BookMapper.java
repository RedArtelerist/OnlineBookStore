package maksym.fedorenko.bookstore.mapper;

import maksym.fedorenko.bookstore.dto.book.BookDto;
import maksym.fedorenko.bookstore.dto.book.BookDtoWithoutCategories;
import maksym.fedorenko.bookstore.dto.book.CreateBookRequestDto;
import maksym.fedorenko.bookstore.dto.book.UpdateBookRequestDto;
import maksym.fedorenko.bookstore.model.Book;
import maksym.fedorenko.bookstore.model.Category;
import org.mapstruct.AfterMapping;
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

    BookDtoWithoutCategories toDtoWithoutCategories(Book book);

    Book toBook(CreateBookRequestDto bookRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBook(
            UpdateBookRequestDto updateBookRequestDto, @MappingTarget Book book
    );

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        bookDto.setCategoryIds(book.getCategories()
                .stream()
                .map(Category::getId)
                .toList()
        );
    }
}
