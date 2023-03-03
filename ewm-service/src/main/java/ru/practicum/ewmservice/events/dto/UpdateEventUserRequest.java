package ru.practicum.ewmservice.events.dto;

import javax.validation.constraints.*;

public class UpdateEventUserRequest extends UpdateEventRequest {
  @Size(min = 20, max = 2000, message = "Длина аннотации должна быть от 20 до 2000 символов.")
  private String annotation;

  @Size(min = 20, max = 7000, message = "Длина описания должна быть от 20 до 7000 символов.")
  private String description;

  @Size(min = 3, max = 120, message = "Длина заголовка должна быть от 3 до 120 символов.")
  private String title;
}