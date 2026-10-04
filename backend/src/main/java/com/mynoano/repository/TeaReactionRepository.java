package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface TeaReactionRepository extends JpaRepository<TeaReaction, Long> {
    Optional<TeaReaction> findByPostIdAndUserIdAndType(Long postId, Long userId, ReactionType type);
    long countByPostIdAndType(Long postId, ReactionType type);
    @Modifying @Query("delete from TeaReaction r where r.postId in (select t.id from TeaPost t where t.expiresAt <= :now)")
    int deleteExpired(@Param("now") Instant now);
}
