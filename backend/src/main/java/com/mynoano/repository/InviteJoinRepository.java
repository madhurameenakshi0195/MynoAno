package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface InviteJoinRepository extends JpaRepository<InviteJoin, Long> {
    Optional<InviteJoin> findByInviteIdAndUserId(Long inviteId, Long userId);
    long countByInviteId(Long inviteId);
    @Modifying @Query("delete from InviteJoin j where j.inviteId in (select i.id from Invite i where i.expiresAt <= :now)")
    int deleteExpired(@Param("now") Instant now);
}
