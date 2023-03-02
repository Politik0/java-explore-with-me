package ru.practicum.ewmservice.events.dto;

import lombok.*;
import ru.practicum.ewmservice.events.model.EventLocation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

  @Size(min = 20, max = 2000, message = "Длина аннотации должна быть от 20 до 2000 символов.")
  @NotNull (message = "Поле с аннотацией не должно быть пустым.")
  @ToString.Exclude
  private String annotation;

  @NotNull (message = "Поле с категорией не должно быть пустым.")
  private long category;

  @NotNull (message = "Поле с описанием не должно быть пустым.")
  @Size(min = 20, max = 7000, message = "Длина описания должна быть от 20 до 7000 символов.")
  @ToString.Exclude
  private String description;

  @NotNull (message = "Поле с датой не должно быть пустым.")
  private String eventDate;

  @NotNull (message = "Локация должна быть указана.")
  private EventLocation location;

  private boolean paid;

  @PositiveOrZero
  private long participantLimit;

  private boolean requestModeration;

  @Size(min = 3, max = 120, message = "Длина заголовка должна быть от 3 до 120 символов.")
  @NotNull (message = "Поле с заголовком не должно быть пустым.")
  private String title;
}
