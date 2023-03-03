package ru.practicum.ewmservice.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputCommentDto {

    @NotNull(message = "Поле с комментарием не должно быть пустым.")
    @Size(min = 20, max = 3000, message = "Длина комментария должна быть от 20 до 3000 символов.")
    private String text;
}
