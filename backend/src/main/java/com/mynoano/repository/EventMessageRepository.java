package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface EventMessageRepository extends JpaRepository<EventMessage, Long> {
    List<EventMessage> findByEventIdOrderByIdAsc(Long eventId);
}
