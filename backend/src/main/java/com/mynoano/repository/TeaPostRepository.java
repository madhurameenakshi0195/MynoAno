package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface TeaPostRepository extends JpaRepository<TeaPost, Long> {
    @Query("select t from TeaPost t where t.expiresAt > :now order by t.id desc")
    List<TeaPost> live(@Param("now") Instant now);
    List<TeaPost> findByAuthorIdAndSourceAndExpiresAtAfterOrderByIdDesc(Long authorId, TeaSource source, Instant now);
    List<TeaPost> findByAuthorIdAndSourceAndNamelessFalseAndExpiresAtAfterOrderByIdDesc(Long authorId, TeaSource source, Instant now);
    @Modifying @Query("delete from TeaPost t where t.expiresAt <= :now")
    int deleteExpired(@Param("now") Instant now);
}
