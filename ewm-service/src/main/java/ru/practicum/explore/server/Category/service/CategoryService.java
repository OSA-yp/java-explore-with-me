package ru.practicum.explore.server.Category.service;

import ru.practicum.explore.server.Category.CategoryDto.CategoryDto;
import ru.practicum.explore.server.Category.CategoryDto.NewCategoryDto;
import ru.practicum.explore.server.Category.CategoryDto.UpdateCategoryDto;

public interface CategoryService {

    CategoryDto create(NewCategoryDto dto);

    CategoryDto update(long id, UpdateCategoryDto dto);

    void delete(long id);
}
