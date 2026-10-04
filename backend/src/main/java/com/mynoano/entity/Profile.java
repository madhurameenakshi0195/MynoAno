package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Profile {
    @Id
    private Long userId;

    private String displayName;

    @Column(length=500)
    private String bio;

    @ElementCollection(fetch=FetchType.EAGER) @Builder.Default
    private Set<String> interests = new HashSet<>();

    @Builder.Default
    private int photoStyle = 0;

    private String intent;

    @Builder.Default
    private boolean discoveryOn = false;

    @Enumerated(EnumType.STRING) @Builder.Default
    private GhostMode ghostMode = GhostMode.ALWAYS;

    private Instant discoveryOffAt;

    private Double lat;

    private Double lng;

    private Instant locationUpdatedAt;
}
