package ru.practicum.ewmservice.events.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.events.dto.EventFullDto;
import ru.practicum.ewmservice.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewmservice.events.service.EventService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    /**
     * Поиск событий администратором
     */
    @GetMapping
    public List<EventFullDto> findEvents(@RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) List<String> states,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("Getting events by admin, users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.findEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    /**
     * Обновление события администратором
     */
    @PatchMapping("/{eventId}")
    public EventFullDto updateEventById(@PathVariable long eventId,
                                        @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Updating event by admin, eventId={}, eventForUpdate={}", eventId, updateEventAdminRequest);
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
}
