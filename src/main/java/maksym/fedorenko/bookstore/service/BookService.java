package maksym.fedorenko.bookstore.service;

import java.util.List;
import maksym.fedorenko.bookstore.dto.BookDto;
import maksym.fedorenko.bookstore.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto getById(Long id);
}
