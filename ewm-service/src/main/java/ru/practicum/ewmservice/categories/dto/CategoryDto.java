package ru.practicum.ewmservice.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private long id;

    @NotNull(message = "Поле с именем категории не должно быть пустым.")
    private String name;
}
