package ru.practicum.ewmservice.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmservice.categories.dto.CategoryDto;
import ru.practicum.ewmservice.events.model.EventLocation;
import ru.practicum.ewmservice.events.model.State;
import ru.practicum.ewmservice.users.dto.UserShortDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
  private long id;

  private String annotation;

  private CategoryDto category;

  private long confirmedRequests;

  private String createdOn;

  private String description;

  private String eventDate;

  private UserShortDto initiator;

  private EventLocation location;

  private boolean paid;

  private long participantLimit;

  private String publishedOn;

  private boolean requestModeration;

  private String title;

  private long views;

  private State state;

  @Override
  public String toString() {
    StringBuilder string = new StringBuilder("Event{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", category=");

    if (category != null) {
      string.append(category.getId());
    } else {
      string.append("null");
    }
    string.append(", eventDate=").append(eventDate).append(", initiator=");

    if (initiator != null) {
      string.append(initiator.getId());
    } else {
      string.append("null");
    }
    return string.append(", location=" + location +
            ", paid=" + paid +
            ", participantLimit=" + participantLimit +
            ", requestModeration=" + requestModeration +
            ", state=" + state +
            ", publishedOn=" + publishedOn +
            ", createdOn=" + createdOn +
            '}').toString();
  }
}
