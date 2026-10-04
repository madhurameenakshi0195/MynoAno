package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="event_messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventMessage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long eventId;

    private Long senderId;

    @Column(length=1000)
    private String body;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
