package ru.practicum.statsservice.service;

import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStats;

import java.util.List;

public interface EndpointHitsService {
    void saveEndpointHi(EndpointHitDto endpointHitDto);

    List<ViewStats> getViewStats(String start, String end, List<String> uris, boolean unique);
}