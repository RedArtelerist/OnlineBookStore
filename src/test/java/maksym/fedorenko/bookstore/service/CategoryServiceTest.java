package maksym.fedorenko.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import maksym.fedorenko.bookstore.dto.category.CategoryDto;
import maksym.fedorenko.bookstore.dto.category.CreateCategoryRequestDto;
import maksym.fedorenko.bookstore.dto.category.UpdateCategoryRequestDto;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.mapper.CategoryMapper;
import maksym.fedorenko.bookstore.mapper.CategoryMapperImpl;
import maksym.fedorenko.bookstore.model.Category;
import maksym.fedorenko.bookstore.repository.CategoryRepository;
import maksym.fedorenko.bookstore.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Spy
    private CategoryMapper categoryMapper = new CategoryMapperImpl();
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Find category by existent id")
    void getById_WithValidId_ShouldReturnValidCategoryDto() {
        Long id = 1L;
        Category category = new Category();
        category.setId(id);
        category.setName("Programming");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        CategoryDto categoryDto = categoryService.getById(id);

        assertThat(categoryDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Programming")
                .hasFieldOrPropertyWithValue("description", null);
    }

    @Test
    @DisplayName("Find category by invalid id")
    void getById_WithInvalidId_ShouldThrowException() {
        Long id = -1L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Can't find category by id: " + id);
    }

    @Test
    @DisplayName("Create new valid category")
    void save_ValidCreateCategoryRequestDto_ReturnCategoryDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Programming", null);

        Category category = new Category();
        category.setName("Programming");

        when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto categoryDto = categoryService.save(requestDto);

        assertThat(categoryDto)
                .hasFieldOrPropertyWithValue("name", "Programming")
                .hasFieldOrPropertyWithValue("description", null);
    }

    @Test
    @DisplayName("Find all categories")
    void findAll_ValidPageable_ReturnAllCategories() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Programming");

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        List<CategoryDto> categoryDtos = categoryService.findAll(pageable);

        assertThat(categoryDtos)
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Programming");
    }

    @Test
    @DisplayName("Update category by existent id")
    void update_ValidCategoryWithExistentId_ReturnUpdatedCategoryDto() {
        Long id = 1L;
        final UpdateCategoryRequestDto requestDto = new UpdateCategoryRequestDto(
                "Programming", null
        );

        Category category = new Category();
        category.setId(id);
        category.setName("Education");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto categoryDto = categoryService.update(id, requestDto);

        assertThat(categoryDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Programming")
                .hasFieldOrPropertyWithValue("description", null);
    }

    @Test
    @DisplayName("Delete category by existent id")
    void delete_WithExistingId_ShouldDoNothing() {
        Long id = anyLong();
        when(categoryRepository.existsById(id)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(id);

        categoryService.delete(id);

        verify(categoryRepository, times(1)).existsById(id);
        verify(categoryRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Update category using invalid id")
    void delete_WithInvalidId_ShouldThrowException() {
        Long id = -1L;
        when(categoryRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.delete(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Category with id=%d doesn't exist".formatted(id));

        verify(categoryRepository, times(1)).existsById(id);
        verifyNoMoreInteractions(categoryRepository);
    }
}
