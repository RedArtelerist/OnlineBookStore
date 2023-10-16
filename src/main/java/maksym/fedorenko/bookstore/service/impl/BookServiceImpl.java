package maksym.fedorenko.bookstore.service.impl;

import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.BookDto;
import maksym.fedorenko.bookstore.dto.BookSearchParametersDto;
import maksym.fedorenko.bookstore.dto.CreateBookRequestDto;
import maksym.fedorenko.bookstore.dto.UpdateBookRequestDto;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.mapper.BookMapper;
import maksym.fedorenko.bookstore.model.Book;
import maksym.fedorenko.bookstore.model.QBook;
import maksym.fedorenko.bookstore.repository.BookRepository;
import maksym.fedorenko.bookstore.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookRepository.save(bookMapper.toBook(requestDto));
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDto> searchBooksByParameters(
            BookSearchParametersDto searchDto, Pageable pageable
    ) {
        return StreamSupport.stream(bookRepository
                        .findAll(searchDto.getFilterPredicate(QBook.book), pageable)
                        .spliterator(), false)
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: " + id));
    }

    @Override
    public BookDto update(Long id, UpdateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: " + id));
        requestDto.updateBook(book);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void delete(Long id) {
        checkIfBookExistsById(id);
        bookRepository.deleteById(id);
    }

    private void checkIfBookExistsById(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book with id=%d doesn't exist".formatted(id));
        }
    }
}
