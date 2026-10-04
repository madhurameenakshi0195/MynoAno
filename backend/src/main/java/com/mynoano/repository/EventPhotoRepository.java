package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface EventPhotoRepository extends JpaRepository<EventPhoto, Long> {
    List<EventPhoto> findByEventIdOrderByIdAsc(Long eventId);
}
