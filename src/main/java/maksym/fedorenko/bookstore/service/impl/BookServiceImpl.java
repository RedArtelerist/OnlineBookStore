package maksym.fedorenko.bookstore.service.impl;

import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.BookDto;
import maksym.fedorenko.bookstore.dto.BookSearchParametersDto;
import maksym.fedorenko.bookstore.dto.CreateBookRequestDto;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.mapper.BookMapper;
import maksym.fedorenko.bookstore.model.Book;
import maksym.fedorenko.bookstore.model.QBook;
import maksym.fedorenko.bookstore.repository.BookRepository;
import maksym.fedorenko.bookstore.service.BookService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookRepository.save(bookMapper.toModel(requestDto));
        return bookMapper.toDto(book);
    }

    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    public List<BookDto> searchBooksByParameters(BookSearchParametersDto searchDto) {
        return StreamSupport.stream(bookRepository
                        .findAll(searchDto.getFilterPredicate(QBook.book))
                        .spliterator(), false)
                .map(bookMapper::toDto)
                .toList();
    }

    public BookDto getById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: " + id));
        return bookMapper.toDto(book);
    }

    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        book.setId(id);
        return bookMapper.toDto(bookRepository.save(book));
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}
