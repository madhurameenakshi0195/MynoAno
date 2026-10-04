package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="event_participants", uniqueConstraints=@UniqueConstraint(columnNames={"event_id","user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventParticipant {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long eventId;

    private Long userId;

    @Builder.Default
    private boolean checkedIn = false;
}
