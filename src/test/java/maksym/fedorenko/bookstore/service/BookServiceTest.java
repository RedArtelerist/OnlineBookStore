package maksym.fedorenko.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import maksym.fedorenko.bookstore.dto.book.BookDto;
import maksym.fedorenko.bookstore.dto.book.CreateBookRequestDto;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.mapper.BookMapper;
import maksym.fedorenko.bookstore.model.Book;
import maksym.fedorenko.bookstore.model.Category;
import maksym.fedorenko.bookstore.repository.BookRepository;
import maksym.fedorenko.bookstore.repository.CategoryRepository;
import maksym.fedorenko.bookstore.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Find book by existent id")
    void getById_WithValidId_ShouldReturnValidBookDto() {
        Long id = 1L;

        Book book = new Book();
        book.setId(id);
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setPrice(BigDecimal.valueOf(10));

        BookDto bookDto = new BookDto(
                book.getId(), book.getTitle(), book.getAuthor(),
                book.getIsbn(), book.getPrice(),
                null, null, null
        );

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.getById(id);
        assertEquals(bookDto, actual);

        verify(bookRepository, times(1)).findById(id);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find book by invalid id")
    void getById_WithInvalidId_ShouldThrowException() {
        Long id = -1L;

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getById(id)
        );
        String expected = "Can't find book by id: " + id;
        assertEquals(expected, exception.getMessage());

        verify(bookRepository, times(1)).findById(id);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Create new valid book")
    void save_ValidCreateBookRequestDto_ReturnBookDto() {
        Long bookId = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Clean Code", "Robert Martin", "1234567890",
                BigDecimal.valueOf(10), null, null,
                List.of(bookId)
        );

        Book book = new Book();
        book.setTitle(requestDto.title());
        book.setAuthor(requestDto.author());
        book.setIsbn(requestDto.isbn());
        book.setPrice(requestDto.price());

        Category category = new Category();
        category.setId(1L);

        BookDto bookDto = new BookDto(
                1L, book.getTitle(), book.getAuthor(),
                book.getIsbn(), book.getPrice(),
                null, null, null
        );

        when(bookMapper.toBook(requestDto)).thenReturn(book);
        when(categoryRepository.getReferenceById(bookId)).thenReturn(category);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto savedBookDto = bookService.save(requestDto);

        assertEquals(bookDto, savedBookDto);
        verify(bookMapper, times(1)).toBook(requestDto);
        verify(categoryRepository, times(1)).getReferenceById(bookId);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(categoryRepository, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Create book with non-existent book id")
    void save_InvalidCreateBookRequestDtoCategoryIdDoesNotExist_ShouldThrowException() {
        Long categoryId = -1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Clean Code", "Robert Martin", "1234567890",
                BigDecimal.valueOf(10), null, null,
                List.of(categoryId)
        );

        Book book = new Book();
        book.setTitle(requestDto.title());
        book.setAuthor(requestDto.author());
        book.setIsbn(requestDto.isbn());
        book.setPrice(requestDto.price());

        when(bookMapper.toBook(requestDto)).thenReturn(book);
        when(categoryRepository.getReferenceById(categoryId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> bookService.save(requestDto));
        verify(bookMapper, times(1)).toBook(requestDto);
        verify(categoryRepository, times(1)).getReferenceById(categoryId);
        verifyNoMoreInteractions(categoryRepository, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Delete book by existent id")
    void delete_WithExistingId_ShouldDoNothing() {
        Long id = anyLong();
        when(bookRepository.existsById(id)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(id);

        bookService.delete(id);

        verify(bookRepository, times(1)).existsById(id);
        verify(bookRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Delete book using invalid id")
    void delete_WithInvalidId_ShouldThrowException() {
        Long id = -1L;
        when(bookRepository.existsById(id)).thenReturn(false);

        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> bookService.delete(id)
        );
        String expected = "Book with id=%d doesn't exist".formatted(id);
        assertEquals(expected, exception.getMessage());

        verify(bookRepository, times(1)).existsById(id);
        verifyNoMoreInteractions(bookRepository);
    }
}
