package ru.practicum.ewmservice.events.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.events.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventIdIn(List<Long> ids, Sort sort);

    List<Comment> findAllByEventId(long eventId, Sort sort);
}