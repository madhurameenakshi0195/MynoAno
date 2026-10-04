package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="invite_joins", uniqueConstraints=@UniqueConstraint(columnNames={"invite_id","user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InviteJoin {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long inviteId;

    private Long userId;
}
