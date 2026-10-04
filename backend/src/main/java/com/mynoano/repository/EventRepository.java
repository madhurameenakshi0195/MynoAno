package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e where e.endsAt > :now and e.closed = false")
    List<Event> active(@Param("now") Instant now);
    List<Event> findByHostIdOrderByIdDesc(Long hostId);
    Optional<Event> findFirstByVenueGroupIgnoreCase(String venueGroup);
}
