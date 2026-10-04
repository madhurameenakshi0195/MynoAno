package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="waves")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Wave {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long fromId;

    private Long toId;

    @Column(length=200)
    private String body;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
