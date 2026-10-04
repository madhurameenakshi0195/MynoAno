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
public class EventService {
    private final EventRepository events;
    private final EventParticipantRepository parts;
    private final EventPhotoRepository photos;
    private final EventMessageRepository chat;
    private final Access access;
    private final ViewFactory views;
    private final FileStorageService storage;

    private boolean visible(Long me, Event e) {
        return e.getVisibility() == Visibility.PUBLIC || e.getHostId().equals(me) || access.anoCircle(me, e.getHostId());
    }

    private boolean open(Event e) { return !e.isClosed() && e.getEndsAt().isAfter(Instant.now()); }

    private Event get(Long me, Long id) {
        Event e = events.findById(id).orElseThrow(() -> ApiException.notFound("Event not found"));
        if (access.blocked(me, e.getHostId()) || !visible(me, e)) throw ApiException.notFound("Event not found");
        return e;
    }

    private Event hostOnly(Long me, Long id) {
        Event e = get(me, id);
        if (!e.getHostId().equals(me)) throw ApiException.forbidden("Only the host can do that");
        return e;
    }

    private EventParticipant participant(Long me, Long id) {
        return parts.findByEventIdAndUserId(id, me).orElseThrow(() -> ApiException.forbidden("Join the event group first"));
    }

    /** Events that have not ended, near a chosen point, grouped by venue and sorted closest first. */
    public List<VenueGroup> list(Long me, double lat, double lng, double radiusMiles) {
        Set<Long> blocked = access.blockedIds(me);
        Map<String, List<Event>> groups = new LinkedHashMap<>();
        events.active(Instant.now()).stream()
                .filter(e -> !blocked.contains(e.getHostId()) && visible(me, e))
                .filter(e -> Geo.miles(lat, lng, e.getLat(), e.getLng()) <= radiusMiles)
                .sorted(Comparator.comparingDouble((Event e) -> Geo.miles(lat, lng, e.getLat(), e.getLng())))
                .forEach(e -> groups.computeIfAbsent(e.getVenueGroup().toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(e));
        return groups.values().stream().map(l -> {
            Event f = l.get(0);
            List<EventCard> cards = l.stream().map(e -> views.eventCard(me, e, lat, lng)).toList();
            return new VenueGroup(f.getVenueGroup(), f.getVenueType(), f.getLat(), f.getLng(), cards.stream().mapToInt(EventCard::going).sum(), cards);
        }).toList();
    }

    public EventDetail detail(Long me, Long id) {
        Event e = get(me, id);
        Optional<EventParticipant> p = parts.findByEventIdAndUserId(id, me);
        return new EventDetail(views.eventCard(me, e, null, null), e.getDescription(), e.getHighlights(),
                (int) parts.countByEventIdAndCheckedInTrue(id), p.map(EventParticipant::isCheckedIn).orElse(false), open(e) && p.isPresent(),
                photos.findByEventIdOrderByIdAsc(id).stream().map(x -> new PhotoView(x.getId(), x.getUrl(), x.getCaption())).toList());
    }

    public List<EventCard> hosted(Long me, Visibility v) {
        return events.findByHostIdOrderByIdDesc(me).stream().filter(e -> e.getVisibility() == v && open(e)).map(e -> views.eventCard(me, e, null, null)).toList();
    }

    /** The creator sets a completion date. Once it passes the event drops out of every list on its own. */
    @Transactional
    public EventCard create(Long me, EventRequest r) {
        Instant now = Instant.now();
        Instant start = r.startsAt() == null ? now : r.startsAt();
        if (!r.endsAt().isAfter(now)) throw ApiException.bad("The completion date must be in the future");
        if (!r.endsAt().isAfter(start)) throw ApiException.bad("The completion date must be after the start");
        String group = r.venueGroup().trim();
        Optional<Event> same = events.findFirstByVenueGroupIgnoreCase(group);
        Event e = events.save(Event.builder().hostId(me).title(r.title().trim()).place(r.place().trim())
                .venueGroup(same.map(Event::getVenueGroup).orElse(group))
                .venueType(same.map(Event::getVenueType).orElse(r.venueType() == null ? "Other" : r.venueType()))
                .lat(same.map(Event::getLat).orElse(r.lat())).lng(same.map(Event::getLng).orElse(r.lng()))
                .startsAt(start).endsAt(r.endsAt()).visibility(r.visibility() == null ? Visibility.PUBLIC : r.visibility())
                .description(r.description()).highlights(r.highlights() == null ? new ArrayList<>() : new ArrayList<>(r.highlights())).build());
        parts.save(EventParticipant.builder().eventId(e.getId()).userId(me).build());
        return views.eventCard(me, e, null, null);
    }

    @Transactional
    public void join(Long me, Long id) {
        Event e = get(me, id);
        if (!open(e)) throw ApiException.bad("This event has ended");
        if (parts.findByEventIdAndUserId(id, me).isEmpty()) parts.save(EventParticipant.builder().eventId(id).userId(me).build());
    }

    @Transactional
    public void leave(Long me, Long id) {
        parts.findByEventIdAndUserId(id, me).ifPresent(parts::delete);
    }

    @Transactional
    public void checkIn(Long me, Long id, boolean in) {
        Event e = get(me, id);
        if (!open(e)) throw ApiException.bad("This event has ended");
        EventParticipant p = participant(me, id);
        p.setCheckedIn(in);
        parts.save(p);
    }

    @Transactional
    public void end(Long me, Long id) {
        Event e = hostOnly(me, id);
        e.setClosed(true);
        events.save(e);
    }

    /** Hosts can add photos from earlier editions at any time. */
    @Transactional
    public PhotoView addPhoto(Long me, Long id, org.springframework.web.multipart.MultipartFile file, String caption) {
        hostOnly(me, id);
        EventPhoto p = photos.save(EventPhoto.builder().eventId(id).url(storage.store(file)).caption(caption).build());
        return new PhotoView(p.getId(), p.getUrl(), p.getCaption());
    }

    public List<EventChatView> chat(Long me, Long id) {
        get(me, id);
        participant(me, id);
        return chat.findByEventIdOrderByIdAsc(id).stream().map(m -> new EventChatView(m.getId(), m.getSenderId(), views.name(m.getSenderId()), m.getBody(), m.getCreatedAt())).toList();
    }

    /** The chat closes automatically when the event ends or the host ends it. */
    @Transactional
    public void postChat(Long me, Long id, String body) {
        Event e = get(me, id);
        participant(me, id);
        if (!open(e)) throw ApiException.bad("The chat closed because the event has ended");
        chat.save(EventMessage.builder().eventId(id).senderId(me).body(body.trim()).build());
    }
}
