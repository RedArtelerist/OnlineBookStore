package maksym.fedorenko.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import maksym.fedorenko.bookstore.model.Category;
import maksym.fedorenko.bookstore.repository.CategoryRepository;
import maksym.fedorenko.bookstore.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Find category by existent id")
    void getById_WithValidId_ShouldReturnValidCategoryDto() {
        Long id = 1L;
        Category category = new Category();
        category.setId(id);
        category.setName("Programming");
        CategoryDto categoryDto = new CategoryDto(id, "Programming", null);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.getById(id);
        assertEquals(categoryDto, actual);

        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find category by invalid id")
    void getById_WithInvalidId_ShouldThrowException() {
        Long id = -1L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(id)
        );
        String expected = "Can't find category by id: " + id;
        assertEquals(expected, exception.getMessage());

        verify(categoryRepository, times(1)).findById(id);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Create new valid category")
    void save_ValidCreateCategoryRequestDto_ReturnCategoryDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Programming", null);

        Category category = new Category();
        category.setName("Programming");

        CategoryDto categoryDto = new CategoryDto(1L, "Programming", null);

        when(categoryMapper.toCategory(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto savedCategoryDto = categoryService.save(requestDto);

        assertEquals(categoryDto, savedCategoryDto);
        verify(categoryMapper, times(1)).toCategory(requestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find all categories")
    void findAll_ValidPageable_ReturnAllCategories() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Effective Java");

        CategoryDto categoryDto = new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        List<CategoryDto> categoryDtos = categoryService.findAll(pageable);

        assertEquals(1, categoryDtos.size());
        assertEquals(categoryDto, categoryDtos.get(0));

        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
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

        Category updatedCategory = new Category();
        updatedCategory.setId(category.getId());
        category.setName("Programming");

        CategoryDto categoryDto = new CategoryDto(1L, "Programming", null);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        doNothing().when(categoryMapper).updateCategory(requestDto, category);
        when(categoryRepository.save(category)).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(categoryDto);

        CategoryDto updatedCategoryDto = categoryService.update(id, requestDto);

        assertEquals(categoryDto, updatedCategoryDto);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, times(1)).updateCategory(requestDto, category);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(updatedCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
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

        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> categoryService.delete(id)
        );
        String expected = "Category with id=%d doesn't exist".formatted(id);
        assertEquals(expected, exception.getMessage());

        verify(categoryRepository, times(1)).existsById(id);
        verifyNoMoreInteractions(categoryRepository);
    }
}
