package ru.practicum.ewmservice.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.events.model.EventLocation;

public interface LocationRepository extends JpaRepository <EventLocation, Long> {
}
