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
public class FriendService {
    private final FriendshipRepository friends;
    private final WaveRepository waves;
    private final ProfileRepository profiles;
    private final Access access;
    private final ViewFactory views;

    private void ensureReachable(Long me, Long target) {
        if (me.equals(target)) throw ApiException.bad("That is you");
        if (!profiles.existsById(target) || access.blocked(me, target)) throw ApiException.notFound("Person not found");
    }

    @Transactional
    public void request(Long me, Long target) {
        ensureReachable(me, target);
        Optional<Friendship> ex = friends.between(me, target);
        if (ex.isPresent()) {
            Friendship f = ex.get();
            if (f.getStatus() == FriendStatus.PENDING && f.getAddresseeId().equals(me)) { f.setStatus(FriendStatus.ACCEPTED); friends.save(f); return; }
            throw ApiException.conflict("Already requested or already friends");
        }
        friends.save(Friendship.builder().requesterId(me).addresseeId(target).build());
    }

    @Transactional
    public void accept(Long me, Long requesterId) {
        Friendship f = friends.between(me, requesterId).filter(x -> x.getStatus() == FriendStatus.PENDING && x.getAddresseeId().equals(me))
                .orElseThrow(() -> ApiException.notFound("No pending request"));
        f.setStatus(FriendStatus.ACCEPTED);
        friends.save(f);
    }

    @Transactional
    public void remove(Long me, Long other) {
        friends.between(me, other).ifPresent(friends::delete);
    }

    public List<FriendView> friends(Long me) {
        return friends.friendsOf(me).stream().map(f -> {
            Long other = f.getRequesterId().equals(me) ? f.getAddresseeId() : f.getRequesterId();
            return new FriendView(views.card(me, other), f.getAnoStatus().name(), me.equals(f.getAnoRequesterId()));
        }).toList();
    }

    public List<FriendView> incoming(Long me) {
        return friends.findByAddresseeIdAndStatus(me, FriendStatus.PENDING).stream()
                .map(f -> new FriendView(views.card(me, f.getRequesterId()), f.getAnoStatus().name(), false)).toList();
    }

    private Friendship accepted(Long me, Long other) {
        return friends.between(me, other).filter(f -> f.getStatus() == FriendStatus.ACCEPTED).orElseThrow(() -> ApiException.bad("You are not friends yet"));
    }

    /** Both people must agree before someone joins the ANO circle. */
    @Transactional
    public void requestAno(Long me, Long friend) {
        Friendship f = accepted(me, friend);
        if (f.getAnoStatus() == AnoStatus.ACCEPTED) return;
        if (f.getAnoStatus() == AnoStatus.REQUESTED && !me.equals(f.getAnoRequesterId())) { f.setAnoStatus(AnoStatus.ACCEPTED); }
        else { f.setAnoStatus(AnoStatus.REQUESTED); f.setAnoRequesterId(me); }
        friends.save(f);
    }

    @Transactional
    public void acceptAno(Long me, Long friend) {
        Friendship f = accepted(me, friend);
        if (f.getAnoStatus() != AnoStatus.REQUESTED || me.equals(f.getAnoRequesterId())) throw ApiException.bad("No ANO request to accept");
        f.setAnoStatus(AnoStatus.ACCEPTED);
        friends.save(f);
    }

    @Transactional
    public void leaveAno(Long me, Long friend) {
        Friendship f = accepted(me, friend);
        f.setAnoStatus(AnoStatus.NONE);
        f.setAnoRequesterId(null);
        friends.save(f);
    }

    @Transactional
    public void wave(Long me, Long target, String text) {
        ensureReachable(me, target);
        waves.save(Wave.builder().fromId(me).toId(target).body(text.trim()).build());
    }

    public List<WaveView> waves(Long me) {
        Set<Long> blocked = access.blockedIds(me);
        return waves.findByToIdOrderByIdDesc(me).stream().filter(w -> !blocked.contains(w.getFromId()))
                .map(w -> new WaveView(w.getId(), views.card(me, w.getFromId()), w.getBody(), w.getCreatedAt())).toList();
    }
}
