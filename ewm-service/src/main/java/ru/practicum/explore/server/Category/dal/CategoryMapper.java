package ru.practicum.explore.server.Category.dal;

import ru.practicum.explore.server.Category.CategoryDto.UpdateCategoryDto;
import ru.practicum.explore.server.Category.model.Category;
import ru.practicum.explore.server.Category.CategoryDto.CategoryDto;
import ru.practicum.explore.server.Category.CategoryDto.NewCategoryDto;

public class CategoryMapper {

    public static CategoryDto toDto(Category category){
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
    public static Category toCategory(NewCategoryDto dto){
        return Category.builder()
                .name(dto.getName())
                .build();
    }
    public static void updateCategory(Category category, UpdateCategoryDto dto) {
        category.setName(dto.getName());
    }

}
