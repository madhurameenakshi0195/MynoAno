package com.mynoano.repository;

import com.mynoano.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("select f from Friendship f where (f.requesterId = :a and f.addresseeId = :b) or (f.requesterId = :b and f.addresseeId = :a)")
    Optional<Friendship> between(@Param("a") Long a, @Param("b") Long b);

    @Query("select f from Friendship f where f.status = com.mynoano.entity.FriendStatus.ACCEPTED and (f.requesterId = :u or f.addresseeId = :u)")
    List<Friendship> friendsOf(@Param("u") Long u);

    List<Friendship> findByAddresseeIdAndStatus(Long addresseeId, FriendStatus status);
}
