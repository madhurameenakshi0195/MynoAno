package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="invites")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Invite {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long authorId;

    @Column(length=140)
    private String body;

    @Builder.Default
    private Instant createdAt = Instant.now();

    private Instant expiresAt;
}
