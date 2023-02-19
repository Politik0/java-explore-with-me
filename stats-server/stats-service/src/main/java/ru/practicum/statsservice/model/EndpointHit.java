package ru.practicum.statsservice.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "endpoint_hits")
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String app;
    @Column
    private String uri;
    @Column
    private String ip;
    @Column
    private LocalDateTime timestamp;
}
