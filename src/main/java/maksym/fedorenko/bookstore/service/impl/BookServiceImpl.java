package maksym.fedorenko.bookstore.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.BookDto;
import maksym.fedorenko.bookstore.dto.CreateBookRequestDto;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.mapper.BookMapper;
import maksym.fedorenko.bookstore.model.Book;
import maksym.fedorenko.bookstore.repository.BookRepository;
import maksym.fedorenko.bookstore.service.BookService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookRepository.save(bookMapper.toModel(requestDto));
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: " + id));
        return bookMapper.toDto(book);
    }
}
