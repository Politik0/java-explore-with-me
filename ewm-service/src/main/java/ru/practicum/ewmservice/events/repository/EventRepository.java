package ru.practicum.ewmservice.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewmservice.events.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Optional<Event> findByIdAndInitiatorId(long id, long initiatorId);

    Page<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);

    long countAllByCategoryId(long categoryId);

    List<Event> findByIdIn(List<Long> ids);
}
