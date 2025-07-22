package ru.practicum.explore.server.comments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class NewCommentDto {

    @NotBlank
    @Length(min = 1, max = 2000)
    private String text;
}
