package ru.practicum.ewmservice.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmservice.events.model.EventLocation;
import ru.practicum.ewmservice.events.model.State;

import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {
    private String annotation;

    private Long category;

    private String description;

    private String eventDate;

    private EventLocation location;

    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration;

    private State stateAction;

    private String title;

}
