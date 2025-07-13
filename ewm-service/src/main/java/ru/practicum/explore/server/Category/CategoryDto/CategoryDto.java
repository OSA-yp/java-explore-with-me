package ru.practicum.explore.server.Category.CategoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long id;
    private String name;
}
