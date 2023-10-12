package maksym.fedorenko.bookstore.mapper;

import maksym.fedorenko.bookstore.config.MapperConfig;
import maksym.fedorenko.bookstore.dto.BookDto;
import maksym.fedorenko.bookstore.dto.CreateBookRequestDto;
import maksym.fedorenko.bookstore.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto bookRequestDto);
}
