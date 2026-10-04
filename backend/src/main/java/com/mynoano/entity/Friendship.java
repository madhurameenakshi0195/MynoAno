package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="friendships", uniqueConstraints=@UniqueConstraint(columnNames={"requester_id","addressee_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Friendship {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long requesterId;

    private Long addresseeId;

    @Enumerated(EnumType.STRING) @Builder.Default
    private FriendStatus status = FriendStatus.PENDING;

    @Enumerated(EnumType.STRING) @Builder.Default
    private AnoStatus anoStatus = AnoStatus.NONE;

    private Long anoRequesterId;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
