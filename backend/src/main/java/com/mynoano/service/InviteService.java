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
public class InviteService {
    private final InviteRepository invites;
    private final InviteJoinRepository joins;
    private final DiscoveryService discovery;
    private final ViewFactory views;

    private static final Set<Integer> ALLOWED = Set.of(30, 60, 120);

    /** Short-lived "chai in 30 minutes?" style invites for people nearby. */
    @Transactional
    public InviteView create(Long me, InviteRequest r) {
        if (!ALLOWED.contains(r.minutes())) throw ApiException.bad("Invites last 30, 60 or 120 minutes");
        Invite i = invites.save(Invite.builder().authorId(me).body(r.text().trim()).expiresAt(Instant.now().plus(r.minutes(), ChronoUnit.MINUTES)).build());
        return view(me, i);
    }

    public List<InviteView> list(Long me) {
        Set<Long> nearby = new HashSet<>(discovery.nearbyIds(me));
        nearby.add(me);
        return invites.live(Instant.now()).stream().filter(i -> nearby.contains(i.getAuthorId())).map(i -> view(me, i)).toList();
    }

    @Transactional
    public InviteView toggleJoin(Long me, Long id) {
        Invite i = invites.findById(id).filter(x -> x.getExpiresAt().isAfter(Instant.now())).orElseThrow(() -> ApiException.notFound("Invite not found"));
        if (i.getAuthorId().equals(me)) throw ApiException.bad("This is your invite");
        Optional<InviteJoin> ex = joins.findByInviteIdAndUserId(id, me);
        if (ex.isPresent()) joins.delete(ex.get()); else joins.save(InviteJoin.builder().inviteId(id).userId(me).build());
        return view(me, i);
    }

    @Transactional
    public void delete(Long me, Long id) {
        Invite i = invites.findById(id).orElseThrow(() -> ApiException.notFound("Invite not found"));
        if (!i.getAuthorId().equals(me)) throw ApiException.forbidden("Not your invite");
        invites.delete(i);
    }

    private InviteView view(Long me, Invite i) {
        return new InviteView(i.getId(), views.name(i.getAuthorId()), i.getAuthorId(), i.getBody(), i.getExpiresAt(),
                joins.countByInviteId(i.getId()) + 1, joins.findByInviteIdAndUserId(i.getId(), me).isPresent(), i.getAuthorId().equals(me));
    }
}
