package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Event {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long hostId;

    private String title;

    private String place;

    private String venueGroup;

    private String venueType;

    private double lat;

    private double lng;

    private Instant startsAt;

    private Instant endsAt;

    @Enumerated(EnumType.STRING) @Builder.Default
    private Visibility visibility = Visibility.PUBLIC;

    @Column(length=2000)
    private String description;

    @ElementCollection(fetch=FetchType.EAGER) @Builder.Default
    private List<String> highlights = new ArrayList<>();

    @Builder.Default
    private boolean closed = false;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
