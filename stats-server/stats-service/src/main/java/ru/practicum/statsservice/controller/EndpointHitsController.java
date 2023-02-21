package ru.practicum.statsservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.service.EndpointHitsService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class EndpointHitsController {
    private final EndpointHitsService endpointHitsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Saving endpoint hit{}", endpointHitDto);
        endpointHitsService.saveEndpointHi(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getViewStats(@RequestParam String start, @RequestParam String end,
                                        @RequestParam(required = false) List<String> uris,
                                        @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Getting view stats from {} till {}, uris={}, unique={}", start, end, uris, unique);
        return endpointHitsService.getViewStats(start, end, uris, unique);
    }
}
