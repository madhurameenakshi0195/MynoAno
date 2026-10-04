package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface WaveRepository extends JpaRepository<Wave, Long> {
    List<Wave> findByToIdOrderByIdDesc(Long toId);
}
