package ru.practicum.ewmservice.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private List<Long> events;

    @Builder.Default
    private Boolean pinned = false;

    @NotNull(message = "Поле с заголовком не должно быть пустым.")
    private String title;
}
