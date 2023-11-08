package maksym.fedorenko.bookstore.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.book.BookDto;
import maksym.fedorenko.bookstore.dto.book.BookDtoWithoutCategories;
import maksym.fedorenko.bookstore.dto.book.BookSearchParametersDto;
import maksym.fedorenko.bookstore.dto.book.CreateBookRequestDto;
import maksym.fedorenko.bookstore.dto.book.UpdateBookRequestDto;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.mapper.BookMapper;
import maksym.fedorenko.bookstore.model.Book;
import maksym.fedorenko.bookstore.repository.BookRepository;
import maksym.fedorenko.bookstore.repository.CategoryRepository;
import maksym.fedorenko.bookstore.repository.book.BookSearchPredicateConstructor;
import maksym.fedorenko.bookstore.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final BookSearchPredicateConstructor bookSearchPredicateConstructor;

    @Override
    @Transactional
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toBook(requestDto);
        addBookCategories(requestDto.categoryIds(), book);
        return bookMapper.toDto(bookRepository.save(book));
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
                        .findAll(bookSearchPredicateConstructor.construct(searchDto), pageable)
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
    @Transactional
    public BookDto update(Long id, UpdateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: " + id));
        bookMapper.updateBook(requestDto, book);
        addBookCategories(requestDto.categoryIds(), book);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void delete(Long id) {
        checkIfBookExistsById(id);
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDtoWithoutCategories> findBooksByCategoryId(Long id, Pageable pageable) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category with id=%d doesn't exist".formatted(id));
        }
        return bookRepository.findAllByCategoriesId(id, pageable)
                .stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    private void addBookCategories(List<Long> categoryIds, Book book) {
        book.setCategories(categoryIds.stream()
                .map(categoryRepository::getReferenceById)
                .collect(Collectors.toSet())
        );
    }

    private void checkIfBookExistsById(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book with id=%d doesn't exist".formatted(id));
        }
    }
}
