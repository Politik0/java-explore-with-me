package ru.practicum.ewmservice.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  private Long id;

  @NotNull(message = "Поле с почтой должно быть заполнено.")
  @Email(message = "Введите верный формат почты.")
  private String email;

  @NotNull(message = "Поле с именем должно быть заполнено.")
  private String name;
}
