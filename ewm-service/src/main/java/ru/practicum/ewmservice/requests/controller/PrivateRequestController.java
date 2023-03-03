package ru.practicum.ewmservice.requests.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.requests.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.requests.service.RequestService;

import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     */
    @GetMapping
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable long userId) {
        log.info("Getting all participation requests, userId={}", userId);
        return requestService.getParticipationRequests(userId);
    }

    /**
     * Добавление заявки от текущего пользователя на участие в событии
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ParticipationRequestDto addParticipationRequest(@PathVariable long userId, @RequestParam long eventId) {
        log.info("Creating new participation request, userId={}, eventId={}", userId, eventId);
        return requestService.addParticipationRequest(userId, eventId);
    }

    /**
     * Отмена своего запроса на участие в событии
     */
    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable long userId, @PathVariable long requestId) {
        log.info("Canceling participation request, userId={}, requestId={}", userId, requestId);
        return requestService.cancelParticipationRequest(userId, requestId);
    }
}
