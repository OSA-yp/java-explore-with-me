package ru.practicum.explore.server.Category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.server.Category.CategoryDto.CategoryDto;
import ru.practicum.explore.server.Category.CategoryDto.NewCategoryDto;
import ru.practicum.explore.server.Category.CategoryDto.UpdateCategoryDto;
import ru.practicum.explore.server.Category.service.CategoryService;

@RestController
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto dto) {
        return categoryService.create(dto);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto update(@PathVariable long catId, @RequestBody @Valid UpdateCategoryDto dto) {
        return categoryService.update(catId, dto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long catId) {
        categoryService.delete(catId);
    }
}
