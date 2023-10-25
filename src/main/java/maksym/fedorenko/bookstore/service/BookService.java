package maksym.fedorenko.bookstore.service;

import java.util.List;
import maksym.fedorenko.bookstore.dto.book.BookDto;
import maksym.fedorenko.bookstore.dto.book.BookDtoWithoutCategories;
import maksym.fedorenko.bookstore.dto.book.BookSearchParametersDto;
import maksym.fedorenko.bookstore.dto.book.CreateBookRequestDto;
import maksym.fedorenko.bookstore.dto.book.UpdateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    List<BookDto> searchBooksByParameters(BookSearchParametersDto searchDto, Pageable pageable);

    BookDto getById(Long id);

    BookDto update(Long id, UpdateBookRequestDto requestDto);

    void delete(Long id);

    List<BookDtoWithoutCategories> findBooksByCategoryId(Long id, Pageable pageable);
}
