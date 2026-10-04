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
public class ProfileService {
    private static final Set<String> INTENTS = Set.of("Chai", "Study", "Walk", "Games", "Food", "Music");
    private final UserRepository users;
    private final ProfileRepository profiles;
    private final TeaPostRepository teaPosts;
    private final EventRepository events;
    private final Access access;
    private final ViewFactory views;

    public MeResponse me(Long uid) {
        User u = users.findById(uid).orElseThrow(() -> ApiException.notFound("User not found"));
        Profile p = profiles.findById(uid).orElse(null);
        if (p == null) return new MeResponse(uid, u.getEmail(), null, false, false, GhostMode.ALWAYS, null);
        return new MeResponse(uid, u.getEmail(), views.card(uid, uid), true, p.isDiscoveryOn(), p.getGhostMode(), p.getDiscoveryOffAt());
    }

    @Transactional
    public MeResponse save(Long uid, ProfileRequest r) {
        Profile p = profiles.findById(uid).orElseGet(() -> Profile.builder().userId(uid).build());
        p.setDisplayName(r.displayName().trim());
        p.setBio(r.bio() == null ? "" : r.bio().trim());
        p.setInterests(r.interests() == null ? new HashSet<>() : new HashSet<>(r.interests()));
        p.setPhotoStyle(r.photoStyle() == null ? 0 : Math.floorMod(r.photoStyle(), 5));
        profiles.save(p);
        return me(uid);
    }

    @Transactional
    public void intent(Long uid, String intent) {
        if (intent != null && !INTENTS.contains(intent)) throw ApiException.bad("Unknown intent");
        Profile p = profiles.findById(uid).orElseThrow(() -> ApiException.bad("Create your profile first"));
        p.setIntent(intent);
        profiles.save(p);
    }

    /** Stub: plug a liveness / face-match provider in here before setting the flag. */
    @Transactional
    public void selfieVerified(Long uid) {
        User u = users.findById(uid).orElseThrow(() -> ApiException.notFound("User not found"));
        u.setSelfieVerified(true);
        users.save(u);
    }

    /** Public view. Nameless tea is never linked to a profile. */
    public PersonProfile view(Long viewer, Long target) {
        if (!viewer.equals(target) && access.blocked(viewer, target)) throw ApiException.notFound("Profile not found");
        PersonCard c = views.card(viewer, target);
        Instant now = Instant.now();
        List<TeaView> posts = teaPosts.findByAuthorIdAndSourceAndNamelessFalseAndExpiresAtAfterOrderByIdDesc(target, TeaSource.MYNO, now)
                .stream().map(t -> views.tea(viewer, t)).toList();
        List<EventCard> evs = events.findByHostIdOrderByIdDesc(target).stream()
                .filter(e -> e.getVisibility() == Visibility.PUBLIC && !e.isClosed() && e.getEndsAt().isAfter(now))
                .map(e -> views.eventCard(viewer, e, null, null)).toList();
        return new PersonProfile(c, posts, evs, access.friendIds(target).size());
    }
}
