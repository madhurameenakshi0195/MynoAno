package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Report {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long reporterId;

    private Long reportedId;

    @Column(length=500)
    private String reason;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
