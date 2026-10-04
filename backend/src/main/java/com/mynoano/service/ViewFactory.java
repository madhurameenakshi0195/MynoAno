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

@Component
@RequiredArgsConstructor
public class ViewFactory {
    private final ProfileRepository profiles;
    private final UserRepository users;
    private final FriendshipRepository friendRepo;
    private final Access access;
    private final EventParticipantRepository parts;
    private final TeaReactionRepository reactions;
    private final TeaPostRepository teaPosts;
    private final EventRepository events;

    public PersonCard card(Long viewer, Long target) {
        Profile p = profiles.findById(target).orElseThrow(() -> ApiException.notFound("Profile not found"));
        User u = users.findById(target).orElseThrow(() -> ApiException.notFound("User not found"));
        Profile me = profiles.findById(viewer).orElse(null);
        Set<String> mine = me == null ? Set.of() : me.getInterests();
        List<String> common = p.getInterests().stream().filter(mine::contains).sorted().toList();
        Set<Long> myFriends = access.friendIds(viewer);
        int mutual = (int) access.friendIds(target).stream().filter(myFriends::contains).count();
        String fs = viewer.equals(target) ? "SELF" : friendRepo.between(viewer, target)
                .map(f -> f.getStatus() == FriendStatus.ACCEPTED ? "FRIEND" : (f.getRequesterId().equals(viewer) ? "SENT" : "RECEIVED"))
                .orElse("NONE");
        boolean match = me != null && me.getIntent() != null && me.getIntent().equalsIgnoreCase(p.getIntent());
        return new PersonCard(target, p.getDisplayName(), p.getBio(), p.getInterests(), p.getPhotoStyle(), p.getIntent(),
                u.isSelfieVerified(), u.isCampusVerified(), u.getCampus(), common, mutual, fs, match);
    }

    public String name(Long userId) {
        return profiles.findById(userId).map(Profile::getDisplayName).orElse("Someone");
    }

    public EventCard eventCard(Long viewer, Event e, Double lat, Double lng) {
        Instant now = Instant.now();
        boolean live = !e.getStartsAt().isAfter(now) && e.getEndsAt().isAfter(now) && !e.isClosed();
        Double d = lat == null || lng == null ? null : Geo.miles(lat, lng, e.getLat(), e.getLng());
        return new EventCard(e.getId(), e.getTitle(), e.getPlace(), e.getVenueGroup(), e.getVenueType(), e.getStartsAt(), e.getEndsAt(),
                live, name(e.getHostId()), e.getHostId(), (int) parts.countByEventId(e.getId()), d, e.getVisibility(),
                parts.findByEventIdAndUserId(e.getId(), viewer).isPresent());
    }

    /** Nameless posts never expose the author id to anyone except the author. */
    public TeaView tea(Long viewer, TeaPost t) {
        boolean mine = t.getAuthorId().equals(viewer);
        String name = t.isNameless() ? "Anonymous" : name(t.getAuthorId());
        Long id = t.getId();
        return new TeaView(id, name, t.isNameless() && !mine ? null : t.getAuthorId(), t.isNameless(), t.getSource(), t.getBody(), t.getEventId(),
                reactions.countByPostIdAndType(id, ReactionType.SPILLED), reactions.countByPostIdAndType(id, ReactionType.SAME),
                reactions.findByPostIdAndUserIdAndType(id, viewer, ReactionType.SPILLED).isPresent(),
                reactions.findByPostIdAndUserIdAndType(id, viewer, ReactionType.SAME).isPresent(), t.getExpiresAt(), mine);
    }

    public Object sharedView(Long viewer, SharedType type, Long id) {
        if (type == SharedType.EVENT) {
            return events.findById(id)
                    .filter(e -> e.getVisibility() == Visibility.PUBLIC || e.getHostId().equals(viewer) || access.anoCircle(viewer, e.getHostId()))
                    .map(e -> eventCard(viewer, e, null, null)).orElse(null);
        }
        if (type == SharedType.TEA) {
            return teaPosts.findById(id).filter(p -> p.getExpiresAt().isAfter(Instant.now()))
                    .filter(p -> p.getSource() == TeaSource.MYNO || p.getAuthorId().equals(viewer) || access.anoCircle(viewer, p.getAuthorId()))
                    .map(p -> tea(viewer, p)).orElse(null);
        }
        return null;
    }
}
