package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="location_shares")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LocationShare {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private Long ownerId;

    private Long trustedId;

    private Double lat;

    private Double lng;

    private Instant expiresAt;

    @Builder.Default
    private Instant updatedAt = Instant.now();
}
