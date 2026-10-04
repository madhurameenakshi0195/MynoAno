package com.mynoano.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="otp_codes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OtpCode {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String codeHash;

    private Instant expiresAt;

    @Builder.Default
    private int attempts = 0;
}
