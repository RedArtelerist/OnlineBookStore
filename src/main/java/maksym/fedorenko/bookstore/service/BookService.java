package maksym.fedorenko.bookstore.service;

import java.util.List;
import maksym.fedorenko.bookstore.dto.BookDto;
import maksym.fedorenko.bookstore.dto.BookSearchParametersDto;
import maksym.fedorenko.bookstore.dto.CreateBookRequestDto;
import maksym.fedorenko.bookstore.dto.UpdateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    List<BookDto> searchBooksByParameters(BookSearchParametersDto searchDto);

    BookDto getById(Long id);

    BookDto update(Long id, UpdateBookRequestDto requestDto);

    void delete(Long id);
}
