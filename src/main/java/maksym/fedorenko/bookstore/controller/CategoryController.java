package maksym.fedorenko.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.book.BookDtoWithoutCategories;
import maksym.fedorenko.bookstore.dto.category.CategoryDto;
import maksym.fedorenko.bookstore.dto.category.CreateCategoryRequestDto;
import maksym.fedorenko.bookstore.dto.category.UpdateCategoryRequestDto;
import maksym.fedorenko.bookstore.service.BookService;
import maksym.fedorenko.bookstore.service.CategoryService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management", description = "Endpoints for mapping categories")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @Operation(
            summary = "Create a new category",
            description = "Create a new category by specifying fields."
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CreateCategoryRequestDto requestDto) {
        return categoryService.save(requestDto);
    }

    @Operation(
            summary = "Retrieve all categories",
            description = "Receiving all categories with pagination and sorting."
    )
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<CategoryDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @Operation(
            summary = "Retrieve a category by id",
            description = "Get a category object by specifying its id."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public CategoryDto getCategoryById(@PathVariable @Positive Long id) {
        return categoryService.getById(id);
    }

    @Operation(
            summary = "Update existing category",
            description = "Update existing category by different fields."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto updateCategory(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateCategoryRequestDto requestDto) {
        return categoryService.update(id, requestDto);
    }

    @Operation(
            summary = "Delete category by id",
            description = "Delete a category object by specifying its id."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable @Positive Long id) {
        categoryService.delete(id);
    }

    @Operation(
            summary = "Retrieve books by category",
            description = """
                    Receiving books by specifying its category id with pagination and sorting."""
    )
    @GetMapping("/{id}/books")
    @PreAuthorize("hasRole('USER')")
    public List<BookDtoWithoutCategories> getBooksByCategoryId(
            @PathVariable @Positive Long id,
            @ParameterObject @PageableDefault Pageable pageable) {
        return bookService.findBooksByCategoryId(id, pageable);
    }
}
