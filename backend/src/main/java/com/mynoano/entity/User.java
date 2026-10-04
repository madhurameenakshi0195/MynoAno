package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false,unique=true)
    private String email;

    @Column(nullable=false,unique=true)
    private String phone;

    @Column(nullable=false)
    private String passwordHash;

    @Builder.Default
    private boolean verified = false;

    @Builder.Default
    private boolean selfieVerified = false;

    @Builder.Default
    private boolean campusVerified = false;

    private String campus;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
