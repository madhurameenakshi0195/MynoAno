package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="tea_reactions", uniqueConstraints=@UniqueConstraint(columnNames={"post_id","user_id","type"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TeaReaction {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private ReactionType type;
}
