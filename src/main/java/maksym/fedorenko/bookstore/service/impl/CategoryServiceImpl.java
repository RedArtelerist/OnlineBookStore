package maksym.fedorenko.bookstore.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import maksym.fedorenko.bookstore.dto.category.CategoryDto;
import maksym.fedorenko.bookstore.dto.category.CreateCategoryRequestDto;
import maksym.fedorenko.bookstore.dto.category.UpdateCategoryRequestDto;
import maksym.fedorenko.bookstore.exception.EntityNotFoundException;
import maksym.fedorenko.bookstore.mapper.CategoryMapper;
import maksym.fedorenko.bookstore.model.Category;
import maksym.fedorenko.bookstore.repository.CategoryRepository;
import maksym.fedorenko.bookstore.service.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto save(CreateCategoryRequestDto requestDto) {
        Category category = categoryMapper.toCategory(requestDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find category by id: " + id
                ));
    }

    @Override
    public CategoryDto update(Long id, UpdateCategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't category book by id: " + id
                ));
        categoryMapper.mapUpdateRequestToCategory(requestDto, category);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        checkIfExistsById(id);
        categoryRepository.deleteById(id);
    }

    private void checkIfExistsById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category with id=%d doesn't exist".formatted(id));
        }
    }
}
