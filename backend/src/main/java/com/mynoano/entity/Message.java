package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Message {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long senderId;

    private Long recipientId;

    @Column(length=2000)
    private String body;

    @Enumerated(EnumType.STRING) @Builder.Default
    private SharedType sharedType = SharedType.NONE;

    private Long sharedId;

    @Builder.Default
    private boolean seen = false;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
