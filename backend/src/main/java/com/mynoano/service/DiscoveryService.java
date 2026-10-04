package com.mynoano.service;

import com.mynoano.dto.Dtos.*;
import com.mynoano.entity.*;
import com.mynoano.exception.ApiException;
import com.mynoano.repository.*;
import com.mynoano.util.Geo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class DiscoveryService {
    private final ProfileRepository profiles;
    private final Access access;
    private final ViewFactory views;

    @org.springframework.beans.factory.annotation.Value("${app.discovery.radius-meters:60}")
    private double radius;
    @org.springframework.beans.factory.annotation.Value("${app.discovery.location-ttl-minutes:10}")
    private long ttlMinutes;

    private Profile profile(Long uid) {
        return profiles.findById(uid).orElseThrow(() -> ApiException.bad("Create your profile first"));
    }

    @Transactional
    public void settings(Long uid, DiscoveryRequest r) {
        Profile p = profile(uid);
        GhostMode g = r.ghostMode() == null ? GhostMode.ALWAYS : r.ghostMode();
        p.setDiscoveryOn(r.on());
        p.setGhostMode(g);
        p.setDiscoveryOffAt(g == GhostMode.TIMED && r.offAfterMinutes() != null ? Instant.now().plus(Math.min(r.offAfterMinutes(), 240), ChronoUnit.MINUTES) : null);
        if (!r.on()) { p.setLat(null); p.setLng(null); p.setLocationUpdatedAt(null); }
        profiles.save(p);
    }

    /** Coordinates are snapped to a ~22 m grid before storing, and never sent back to other users. */
    @Transactional
    public void location(Long uid, LocationRequest r) {
        Profile p = profile(uid);
        if (!p.isDiscoveryOn()) return;
        p.setLat(snap(r.lat()));
        p.setLng(snap(r.lng()));
        p.setLocationUpdatedAt(Instant.now());
        profiles.save(p);
    }

    private static double snap(double v) { return Math.round(v / 0.0002) * 0.0002; }

    public Set<Long> nearbyIds(Long uid) {
        Profile me = profile(uid);
        if (!me.isDiscoveryOn() || me.getLat() == null || me.getGhostMode() == GhostMode.EVENTS_ONLY) return Set.of();
        double dLat = (radius + 30) / 111_000.0;
        double dLng = (radius + 30) / (111_000.0 * Math.max(0.1, Math.cos(Math.toRadians(me.getLat()))));
        Set<Long> blocked = access.blockedIds(uid);
        return profiles.findNearby(me.getLat() - dLat, me.getLat() + dLat, me.getLng() - dLng, me.getLng() + dLng,
                        Instant.now().minus(ttlMinutes, ChronoUnit.MINUTES), uid).stream()
                .filter(p -> p.getGhostMode() != GhostMode.EVENTS_ONLY)
                .filter(p -> !blocked.contains(p.getUserId()))
                .filter(p -> Geo.meters(me.getLat(), me.getLng(), p.getLat(), p.getLng()) <= radius)
                .map(Profile::getUserId).collect(Collectors.toSet());
    }

    /** Returns people only. No distance or coordinates, and the order never reflects proximity. */
    public List<PersonCard> nearby(Long uid) {
        return nearbyIds(uid).stream().map(id -> views.card(uid, id))
                .sorted(Comparator.comparing(PersonCard::intentMatch).reversed()
                        .thenComparing(c -> -c.commonInterests().size()).thenComparing(PersonCard::displayName))
                .toList();
    }
}
