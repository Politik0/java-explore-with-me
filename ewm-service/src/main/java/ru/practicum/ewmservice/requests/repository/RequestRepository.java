package ru.practicum.ewmservice.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewmservice.requests.dto.RequestWithCount;
import ru.practicum.ewmservice.requests.model.Request;
import ru.practicum.ewmservice.requests.model.Status;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long>, QuerydslPredicateExecutor<Request> {
    long countAllByEventIdAndStatus(long eventId, Status status);

    List<Request> findAllByRequesterId(long requesterId);

    Optional<Request> findByRequesterIdAndEventId(long requesterId, long eventId);

    Optional<Request> findByIdAndRequesterId(long id, long requesterId);

    List<Request> findAllByEventId(long eventId);

    List<Request> findAllByIdInAndEventId(List<Long> ids, long eventId);

    @Query("select new ru.practicum.ewmservice.requests.dto.RequestWithCount(r.event.id, count(r.requester)) " +
            "from Request r where r.event.id in ?1 and r.status = ?2 group by r.event.id")
    List<RequestWithCount> getRequestCount(List<Long> ids, Status status);
}
