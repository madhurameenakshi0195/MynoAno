package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="tea_posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TeaPost {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long authorId;

    @Column(length=2000)
    private String body;

    @Enumerated(EnumType.STRING)
    private TeaSource source;

    @Builder.Default
    private boolean nameless = false;

    private Long eventId;

    @Builder.Default
    private Instant createdAt = Instant.now();

    private Instant expiresAt;
}
