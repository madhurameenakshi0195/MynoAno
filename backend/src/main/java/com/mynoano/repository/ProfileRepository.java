package com.mynoano.repository;

import com.mynoano.entity.Profile;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    @Query("select p from Profile p where p.discoveryOn = true and p.lat between :minLat and :maxLat and p.lng between :minLng and :maxLng and p.locationUpdatedAt > :since and p.userId <> :me")
    List<Profile> findNearby(@Param("minLat") double minLat, @Param("maxLat") double maxLat, @Param("minLng") double minLng, @Param("maxLng") double maxLng, @Param("since") Instant since, @Param("me") Long me);

    List<Profile> findByDiscoveryOnTrueAndDiscoveryOffAtBefore(Instant t);
}
