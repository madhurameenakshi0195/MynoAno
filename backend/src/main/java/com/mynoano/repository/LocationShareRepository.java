package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface LocationShareRepository extends JpaRepository<LocationShare, Long> {
    Optional<LocationShare> findByOwnerId(Long ownerId);
    List<LocationShare> findByTrustedIdAndExpiresAtAfter(Long trustedId, Instant now);
    @Modifying @Query("delete from LocationShare s where s.expiresAt <= :now")
    int deleteExpired(@Param("now") Instant now);
}
