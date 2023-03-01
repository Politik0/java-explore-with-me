package ru.practicum.ewmservice.requests.model;

import lombok.*;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("Request{" +
                "id=" + id +
                ", created=" + created +
                ", event=");
        if (event != null) {
            string.append(event.getId());
        } else {
            string.append("null");
        }
        if (requester != null) {
            string.append(", requester=").append(requester.getId());
        } else {
            string.append(", requester=null");
        }
        return string.append(", status=").append(status).append('}').toString();
    }
}
