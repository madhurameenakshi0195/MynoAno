package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface InviteRepository extends JpaRepository<Invite, Long> {
    @Query("select i from Invite i where i.expiresAt > :now order by i.id desc")
    List<Invite> live(@Param("now") Instant now);
    @Modifying @Query("delete from Invite i where i.expiresAt <= :now")
    int deleteExpired(@Param("now") Instant now);
}
