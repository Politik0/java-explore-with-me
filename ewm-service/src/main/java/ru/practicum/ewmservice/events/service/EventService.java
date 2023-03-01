package ru.practicum.ewmservice.events.service;

import ru.practicum.ewmservice.events.dto.*;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewmservice.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewmservice.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<EventShortDto> getAllEventsOfCurrentUser(long userId, int from, int size);

    EventFullDto addNewEvent(long userId, NewEventDto newEventDto);

    EventFullDto getEventByIdOfCurrentUser(long userId, long eventId);

    EventFullDto getPublicEventInfoById(long id, HttpServletRequest request);

    Event getEventById(long eventId);

    EventFullDto updateEventOfCurrentUser(long userId, long eventId, UpdateEventUserRequest eventUserRequest);

    List<EventShortDto> getAllEventsWithFilter(String text, List<Long> categories, Boolean paid, String rangeStart,
                                     String rangeEnd, String sort, Boolean onlyAvailable, int from, int size,
                                               HttpServletRequest request);

    List<EventShortDto> getEventShortDtos(List<Event> events);

    List<Event> getEventsByIds(List<Long> ids);

    List<EventFullDto> findEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                  String rangeStart, String rangeEnd, int from, int size);

    List<ParticipationRequestDto> getEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(long userId, long eventId,
                                                       EventRequestStatusUpdateRequest requestStatus);

    EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
