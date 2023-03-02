package ru.practicum.ewmservice.events.model;

import lombok.*;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.compilations.model.Compilation;
import ru.practicum.ewmservice.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 120, nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String annotation;

    @Column(length = 7000, nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private EventLocation location;

    @Column
    private boolean paid;

    @Column(name = "participant_limit")
    private long participantLimit;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @ManyToMany(mappedBy = "events")
    private List<Compilation> compilations;

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category=");

        if (category != null) {
            string.append(category.getId());
        } else {
            string.append("null");
        }
        string.append(", eventDate=").append(eventDate).append(", initiator=");

         if (initiator != null) {
             string.append(initiator.getId());
         } else {
             string.append("null");
         }

        return string.append(", location=" + location +
                ", paid=" + paid +
                ", participantLimit=" + participantLimit +
                ", requestModeration=" + requestModeration +
                ", state=" + state +
                ", publishedOn=" + publishedOn +
                ", createdOn=" + createdOn +
                '}').toString();
    }
}
