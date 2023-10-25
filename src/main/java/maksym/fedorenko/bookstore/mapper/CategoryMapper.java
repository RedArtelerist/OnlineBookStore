package maksym.fedorenko.bookstore.mapper;

import maksym.fedorenko.bookstore.dto.category.CategoryDto;
import maksym.fedorenko.bookstore.dto.category.CreateCategoryRequestDto;
import maksym.fedorenko.bookstore.dto.category.UpdateCategoryRequestDto;
import maksym.fedorenko.bookstore.model.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toCategory(CreateCategoryRequestDto requestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategory(
            UpdateCategoryRequestDto updateBookRequestDto, @MappingTarget Category category
    );
}
