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
public class TeaService {
    private final TeaPostRepository teaPosts;
    private final TeaReactionRepository reactions;
    private final EventRepository events;
    private final Access access;
    private final ViewFactory views;

    /** Every post disappears after 24 hours. */
    @Transactional
    public TeaView post(Long me, TeaRequest r) {
        if (r.eventId() != null && !events.existsById(r.eventId())) throw ApiException.notFound("Event not found");
        TeaPost t = teaPosts.save(TeaPost.builder().authorId(me).body(r.body().trim()).source(r.source()).nameless(r.nameless())
                .eventId(r.eventId()).expiresAt(Instant.now().plus(24, ChronoUnit.HOURS)).build());
        return views.tea(me, t);
    }

    /** MYNO tea is public. ANO tea is visible to its author and their ANO circle only. */
    public List<TeaView> feed(Long me, String filter) {
        Set<Long> blocked = access.blockedIds(me);
        Set<Long> ano = access.anoIds(me);
        return teaPosts.live(Instant.now()).stream()
                .filter(t -> !blocked.contains(t.getAuthorId()))
                .filter(t -> t.getSource() == TeaSource.MYNO || t.getAuthorId().equals(me) || ano.contains(t.getAuthorId()))
                .filter(t -> filter == null || filter.isBlank() || filter.equalsIgnoreCase("all") || t.getSource().name().equalsIgnoreCase(filter))
                .map(t -> views.tea(me, t)).toList();
    }

    public List<TeaView> mine(Long me, TeaSource source) {
        return teaPosts.findByAuthorIdAndSourceAndExpiresAtAfterOrderByIdDesc(me, source, Instant.now()).stream().map(t -> views.tea(me, t)).toList();
    }

    @Transactional
    public TeaView react(Long me, Long postId, ReactionType type) {
        TeaPost t = teaPosts.findById(postId).filter(x -> x.getExpiresAt().isAfter(Instant.now())).orElseThrow(() -> ApiException.notFound("Post not found"));
        if (access.blocked(me, t.getAuthorId())) throw ApiException.notFound("Post not found");
        Optional<TeaReaction> ex = reactions.findByPostIdAndUserIdAndType(postId, me, type);
        if (ex.isPresent()) reactions.delete(ex.get());
        else reactions.save(TeaReaction.builder().postId(postId).userId(me).type(type).build());
        return views.tea(me, t);
    }

    @Transactional
    public void delete(Long me, Long postId) {
        TeaPost t = teaPosts.findById(postId).orElseThrow(() -> ApiException.notFound("Post not found"));
        if (!t.getAuthorId().equals(me)) throw ApiException.forbidden("Not your post");
        teaPosts.delete(t);
    }
}
