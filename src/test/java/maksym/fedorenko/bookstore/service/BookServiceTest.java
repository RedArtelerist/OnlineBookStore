package maksym.fedorenko.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import maksym.fedorenko.bookstore.dto.book.BookDto;
import maksym.fedorenko.bookstore.dto.book.CreateBookRequestDto;
import maksym.fedorenko.bookstore.dto.book.UpdateBookRequestDto;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.mapper.BookMapper;
import maksym.fedorenko.bookstore.mapper.BookMapperImpl;
import maksym.fedorenko.bookstore.model.Book;
import maksym.fedorenko.bookstore.model.Category;
import maksym.fedorenko.bookstore.repository.BookRepository;
import maksym.fedorenko.bookstore.repository.CategoryRepository;
import maksym.fedorenko.bookstore.service.impl.BookServiceImpl;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Spy
    private BookMapper bookMapper = new BookMapperImpl();
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

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        BookDto bookDto = bookService.getById(id);
        assertThat(bookDto)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("title", "Clean Code")
                .hasFieldOrPropertyWithValue("author", "Robert Martin")
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(10));
    }

    @Test
    @DisplayName("Find book by invalid id")
    void getById_WithInvalidId_ShouldThrowException() {
        Long id = -1L;

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Can't find book by id: " + id);
    }

    @Test
    @DisplayName("Create new valid book")
    void save_ValidCreateBookRequestDto_ReturnBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Clean Code",
                "Robert Martin",
                "1234567890",
                BigDecimal.valueOf(10),
                null,
                null,
                List.of(1L, 2L)
        );

        Category category1 = new Category();
        category1.setId(1L);
        Category category2 = new Category();
        category2.setId(2L);

        Book book = new Book();
        book.setTitle(requestDto.title());
        book.setAuthor(requestDto.author());
        book.setIsbn(requestDto.isbn());
        book.setPrice(requestDto.price());
        book.setCategories(Set.of(category1, category2));

        when(categoryRepository.getReferenceById(anyLong())).thenReturn(category1, category2);
        when(bookRepository.save(book)).thenReturn(book);

        BookDto bookDto = bookService.save(requestDto);

        assertThat(bookDto)
                .hasFieldOrPropertyWithValue("title", "Clean Code")
                .hasFieldOrPropertyWithValue("author", "Robert Martin")
                .hasFieldOrPropertyWithValue("isbn", "1234567890")
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(10))
                .extracting("categoryIds")
                .asInstanceOf(InstanceOfAssertFactories.list(Long.class))
                .containsExactlyInAnyOrder(1L, 2L);
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

        when(categoryRepository.getReferenceById(categoryId))
                .thenThrow(EntityNotFoundException.class);

        assertThatThrownBy(() -> bookService.save(requestDto))
                .isInstanceOf(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> bookService.save(requestDto));
    }

    @Test
    @DisplayName("Update book by existent id")
    void update_WithExistingId_ReturnBookDto() {
        final Long id = 1L;
        final UpdateBookRequestDto requestDto = new UpdateBookRequestDto(
                "Effective Java",
                "Joshua Bloch",
                "1234567890",
                BigDecimal.valueOf(30),
                null,
                null,
                Collections.emptyList()
        );

        Category category = new Category();
        category.setId(1L);
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setIsbn("123456789");
        book.setPrice(BigDecimal.valueOf(30));
        book.setCategories(Set.of(category));

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        BookDto bookDto = bookService.update(id, requestDto);

        assertThat(bookDto)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("title", "Effective Java")
                .hasFieldOrPropertyWithValue("author", "Joshua Bloch")
                .hasFieldOrPropertyWithValue("isbn", "1234567890")
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(30))
                .hasFieldOrPropertyWithValue("categoryIds", Collections.emptyList());
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

        assertThatThrownBy(() -> bookService.delete(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Book with id=%d doesn't exist".formatted(id));

        verify(bookRepository, times(1)).existsById(id);
        verifyNoMoreInteractions(bookRepository);
    }
}
