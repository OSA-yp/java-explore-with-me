package ru.practicum.explore.server.Category.CategoryDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryDto {

    @NotBlank
    @Size(max = 50)
    private String name;
}