package maksym.fedorenko.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.BookDto;
import maksym.fedorenko.bookstore.dto.BookSearchParametersDto;
import maksym.fedorenko.bookstore.dto.CreateBookRequestDto;
import maksym.fedorenko.bookstore.dto.UpdateBookRequestDto;
import maksym.fedorenko.bookstore.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for mapping books")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @Operation(
            summary = "Retrieve all books",
            description = "Receiving all books with pagination and sorting by fields."
    )
    @GetMapping
    public List<BookDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(
            summary = "Retrieve a book by id",
            description = "Get a book object by specifying its id."
    )
    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable @Positive Long id) {
        return bookService.getById(id);
    }

    @Operation(
            summary = "Create a new book",
            description = "Create a new book by specifying fields."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.save(bookDto);
    }

    @Operation(
            summary = "Update existing book",
            description = "Update existing book by different fields."
    )
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BookDto updateBook(
            @PathVariable @Positive Long id, @RequestBody @Valid UpdateBookRequestDto bookDto
    ) {
        return bookService.update(id, bookDto);
    }

    @Operation(
            summary = "Delete book by id",
            description = "Delete a book object by specifying its id."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable @Positive Long id) {
        bookService.delete(id);
    }

    @Operation(
            summary = "Search books by different parameters",
            description = "Search books by title, specify a selection of authors, "
                    + "and indicate the minimum and maximum price ranges."
    )
    @GetMapping("/search")
    public List<BookDto> searchBooks(
            @Valid BookSearchParametersDto searchParameters, Pageable pageable
    ) {
        return bookService.searchBooksByParameters(searchParameters, pageable);
    }
}
