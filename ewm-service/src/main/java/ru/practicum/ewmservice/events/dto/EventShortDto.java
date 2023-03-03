package ru.practicum.ewmservice.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmservice.categories.dto.CategoryDto;
import ru.practicum.ewmservice.users.dto.UserShortDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
  private long id;

  private String annotation;

  private CategoryDto category;

  private long confirmedRequests;

  private String eventDate;

  private UserShortDto initiator;

  private boolean paid;

  private String title;

  private long views;
}
