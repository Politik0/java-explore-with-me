package ru.practicum.statsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitsRepository extends JpaRepository<EndpointHit, Integer> {

    @Query("select new ru.practicum.statsdto.ViewStats(eh.app, eh.uri, count (eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.timestamp between ?1 and ?2 and eh.uri in ?3 " +
            "group by eh.uri, eh.app " +
            "order by count (eh.ip) desc")
    List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.statsdto.ViewStats(eh.app, eh.uri, count (distinct eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.timestamp between ?1 and ?2 and eh.uri in ?3 " +
            "group by eh.uri, eh.app " +
            "order by count (eh.ip) desc")
    List<ViewStats> getViewStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.statsdto.ViewStats(eh.app, eh.uri, count (eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.timestamp between ?1 and ?2 and eh.uri like %?3% " +
            "group by eh.uri, eh.app " +
            "order by count (eh.ip) desc")
    List<ViewStats> getViewStatsAll(LocalDateTime start, LocalDateTime end, String uri);

    @Query("select new ru.practicum.statsdto.ViewStats(eh.app, eh.uri, count (distinct eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.timestamp between ?1 and ?2 and eh.uri like %?3% " +
            "group by eh.uri, eh.app " +
            "order by count (eh.ip) desc")
    List<ViewStats> getViewStatsAllUnique(LocalDateTime start, LocalDateTime end, String uri);
}
