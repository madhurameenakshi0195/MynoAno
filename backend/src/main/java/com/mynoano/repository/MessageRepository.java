package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select m from Message m where (m.senderId = :a and m.recipientId = :b) or (m.senderId = :b and m.recipientId = :a) order by m.id")
    List<Message> thread(@Param("a") Long a, @Param("b") Long b);

    @Query("select m from Message m where m.senderId = :u or m.recipientId = :u order by m.id desc")
    List<Message> mine(@Param("u") Long u);
}
