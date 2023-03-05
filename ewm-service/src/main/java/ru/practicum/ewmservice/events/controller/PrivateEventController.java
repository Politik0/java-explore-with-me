package ru.practicum.ewmservice.events.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.events.dto.*;
import ru.practicum.ewmservice.events.service.EventService;
import ru.practicum.ewmservice.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewmservice.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewmservice.requests.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;

    /**
     * Получение событий, добавленных текущим пользователем
     */
    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable long userId, @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all events by user with id={}, from={}, size={}", userId, from, size);
        return eventService.getAllEventsOfCurrentUser(userId, from, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDto addNewEvent(@PathVariable long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Creating new event by user with id={}, newEventDto={}", userId, newEventDto.toString());
        return eventService.addNewEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Getting event by id by user with id={}, eventId={}", userId, eventId);
        return eventService.getEventByIdOfCurrentUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable long userId, @PathVariable long eventId,
                            @RequestBody @Valid UpdateEventUserRequest eventUserRequest) {
        log.info("Updating event, userId={}, eventId={}, eventForUpdate={}", userId, eventId, eventUserRequest);
        return eventService.updateEventOfCurrentUser(userId, eventId, eventUserRequest);
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя
     */
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Getting all event requests, userId={}, eventId={}", userId, eventId);
        return eventService.getEventRequests(userId, eventId);
    }

    /**
     * Изменение статуса запроса на участие в событии текущего пользователя
     */
    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable long userId,
                                                              @PathVariable long eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest updateRequestStatus) {
        log.info("Updating request status, userId={}, eventId={}, requestStatus={}", userId, eventId,
                updateRequestStatus);
        return eventService.updateRequestStatus(userId, eventId, updateRequestStatus);
    }

    /**
     * Добавление зарегистрированным пользователем комментария к событию
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{eventId}/comment")
    public CommentDto addComment(@PathVariable long userId, @PathVariable long eventId,
                                 @RequestBody @Valid InputCommentDto commentDto) {
        log.info("Posting comment by user with id={} to event with id={}, commentDto={}", userId, eventId, commentDto);
        return eventService.addComment(userId, eventId, commentDto);
    }

    /**
     * Изменение комментария в течение суток
     */
    @PatchMapping("/{eventId}/comment/{commentId}")
    public CommentDto updateComment(@PathVariable long userId, @PathVariable long eventId, @PathVariable long commentId,
                                    @RequestBody @Valid InputCommentDto commentDto) {
        log.info("Updating comment with id={} by user with id={} to event with id={}, commentDto={}", commentId, userId, eventId, commentDto);
        return eventService.updateComment(userId, eventId, commentId, commentDto);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{eventId}/comment/{commentId}")
    public void deleteComment(@PathVariable long userId, @PathVariable long eventId, @PathVariable long commentId) {
        log.info("Deleting comment with id={} by user with id={} to event with id={}", commentId, userId, eventId);
        eventService.deleteComment(userId, eventId, commentId);
    }
}
