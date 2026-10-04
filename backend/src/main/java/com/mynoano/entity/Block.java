package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="blocks", uniqueConstraints=@UniqueConstraint(columnNames={"blocker_id","blocked_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Block {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long blockerId;

    private Long blockedId;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
