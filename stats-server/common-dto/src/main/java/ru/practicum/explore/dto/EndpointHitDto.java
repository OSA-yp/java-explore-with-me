package ru.practicum.explore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {
    @NotBlank
    private String app;

    @NotBlank
    @Size(max = 1024)
    private String uri;

    @NotBlank
    private String ip;

    private String timestamp;
}