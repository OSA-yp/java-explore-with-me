package ru.practicum.explore.server.Category.service;

import ru.practicum.explore.server.Category.CategoryDto.CategoryResponseDto;
import ru.practicum.explore.server.Category.CategoryDto.NewCategoryDto;
import ru.practicum.explore.server.Category.CategoryDto.UpdateCategoryDto;

import java.util.Collection;

public interface CategoryService {

    CategoryResponseDto create(NewCategoryDto dto);

    Collection<CategoryResponseDto> getCategories(Collection<Long> ids, Integer from, Integer size);

    CategoryResponseDto getById(Long id);

    CategoryResponseDto update(long id, UpdateCategoryDto dto);

    void delete(long id);
}
