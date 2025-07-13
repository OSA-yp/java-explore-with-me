package ru.practicum.explore.server.Category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.server.Category.CategoryDto.CategoryDto;
import ru.practicum.explore.server.Category.CategoryDto.NewCategoryDto;
import ru.practicum.explore.server.Category.CategoryDto.UpdateCategoryDto;
import ru.practicum.explore.server.Category.dal.CategoryMapper;
import ru.practicum.explore.server.Category.dal.CategoryRepository;
import ru.practicum.explore.server.Category.model.Category;
import ru.practicum.explore.server.exception.ConflictException;
import ru.practicum.explore.server.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public CategoryDto create(NewCategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())){
            throw new ConflictException("Категория с таким именем уже есть");
        }
        Category newCategory = categoryRepository.save(CategoryMapper.toCategory(dto));
        return CategoryMapper.toDto(newCategory);
    }

    @Override
    public CategoryDto update(long id, UpdateCategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + id + " не найдена"));
        if (categoryRepository.existsByName(dto.getName())){
            throw new ConflictException("Категория с таким именем уже есть");
        }
        CategoryMapper.updateCategory(category,dto);
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void delete(long id) {
        // когда появятся события — проверять отсутствие связей
        categoryRepository.deleteById(id);

    }
}
