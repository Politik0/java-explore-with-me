package ru.practicum.statsservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.mapper.EndpointHitMapper;
import ru.practicum.statsservice.mapper.StringDateConverter;
import ru.practicum.statsservice.model.EndpointHit;
import ru.practicum.statsservice.repository.EndpointHitsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EndpointHitsServiceImpl implements EndpointHitsService {
    private final EndpointHitsRepository endpointHitsRepository;
    private final EndpointHitMapper endpointHitMapper;
    private final StringDateConverter converter;


    @Override
    public void saveEndpointHi(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHitSaved = endpointHitsRepository.save(endpointHitMapper.convertFromDto(endpointHitDto));
        log.info("EndpointHit saved, {}", endpointHitSaved);
    }

    @Override
    public List<ViewStats> getViewStats(String startValue, String endValue, List<String> uris, boolean unique) {
        List<ViewStats> viewStats;
        LocalDateTime start = converter.convert(startValue);
        LocalDateTime end = converter.convert(endValue);
        if (uris == null && unique) {
            viewStats = endpointHitsRepository.getViewStatsAllUnique(start, end, "/");
            log.info("Unique viewStats (without uris) got, {}", viewStats.toString());
        } else if (uris == null) {
            viewStats = endpointHitsRepository.getViewStatsAll(start, end, "/");
            log.info("Not unique viewStats (without uris) got, {}", viewStats.toString());
        } else if (unique) {
            viewStats = endpointHitsRepository.getViewStatsUnique(start, end, uris);
            log.info("Unique viewStats (with uris) got, {}", viewStats.toString());
        } else {
            viewStats = endpointHitsRepository.getViewStats(start, end, uris);
            log.info("Not unique ViewStats (with uris) got, {}", viewStats.toString());
        }
        return viewStats;
    }
}