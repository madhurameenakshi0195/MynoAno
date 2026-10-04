package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    Optional<EventParticipant> findByEventIdAndUserId(Long eventId, Long userId);
    long countByEventId(Long eventId);
    long countByEventIdAndCheckedInTrue(Long eventId);
}
