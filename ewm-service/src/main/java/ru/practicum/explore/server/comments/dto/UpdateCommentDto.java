package ru.practicum.explore.server.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCommentDto {

    @NotBlank
    @Size(min = 1, max = 2000)
    private String comment;
}
