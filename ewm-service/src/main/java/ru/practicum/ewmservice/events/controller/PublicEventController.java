package ru.practicum.ewmservice.events.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.events.dto.EventFullDto;
import ru.practicum.ewmservice.events.dto.EventShortDto;
import ru.practicum.ewmservice.events.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class PublicEventController {
    private final EventService eventService;

    /**
     * Получение событий с возможностю фильтрации
     */
    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        log.info("Getting events, text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, sort={}, " +
                        "onlyAvailable={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, sort, onlyAvailable, from, size);
        return eventService.getAllEventsWithFilter(text, categories, paid, rangeStart, rangeEnd,
                sort, onlyAvailable, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable long id, HttpServletRequest request) {
        log.info("Getting event by id, id={}", id);
        return eventService.getPublicEventInfoById(id, request);
    }
}
