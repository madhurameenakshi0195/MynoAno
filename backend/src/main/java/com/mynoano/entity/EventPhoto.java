package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="event_photos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventPhoto {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long eventId;

    private String url;

    private String caption;

    @Builder.Default
    private Instant uploadedAt = Instant.now();
}
