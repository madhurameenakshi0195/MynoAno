package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByUserIdOrderByIdDesc(Long userId);
    void deleteByUserId(Long userId);
}
