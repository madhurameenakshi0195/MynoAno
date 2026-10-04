package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface BlockRepository extends JpaRepository<Block, Long> {
    @Query("select count(b) > 0 from Block b where (b.blockerId = :a and b.blockedId = :b) or (b.blockerId = :b and b.blockedId = :a)")
    boolean either(@Param("a") Long a, @Param("b") Long b);
    @Query("select b from Block b where b.blockerId = :u or b.blockedId = :u")
    List<Block> involving(@Param("u") Long u);
    Optional<Block> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
    List<Block> findByBlockerId(Long blockerId);
}
