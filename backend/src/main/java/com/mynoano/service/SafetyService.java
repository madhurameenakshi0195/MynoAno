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
public class SafetyService {
    private final BlockRepository blocks;
    private final ReportRepository reports;
    private final FriendshipRepository friendRepo;
    private final LocationShareRepository shares;
    private final ProfileRepository profiles;
    private final Access access;

    /** Blocking is two-way: neither person can see the other anywhere, including Nearby. */
    @Transactional
    public void block(Long me, Long target) {
        if (me.equals(target)) throw ApiException.bad("You cannot block yourself");
        if (!profiles.existsById(target)) throw ApiException.notFound("Person not found");
        if (blocks.findByBlockerIdAndBlockedId(me, target).isEmpty()) blocks.save(Block.builder().blockerId(me).blockedId(target).build());
        friendRepo.between(me, target).ifPresent(friendRepo::delete);
        shares.findByOwnerId(me).filter(s -> s.getTrustedId().equals(target)).ifPresent(shares::delete);
    }

    @Transactional
    public void unblock(Long me, Long target) {
        blocks.findByBlockerIdAndBlockedId(me, target).ifPresent(blocks::delete);
    }

    public List<BlockedView> blocked(Long me) {
        return blocks.findByBlockerId(me).stream().map(b -> new BlockedView(b.getBlockedId(), profiles.findById(b.getBlockedId()).map(Profile::getDisplayName).orElse("Someone"))).toList();
    }

    @Transactional
    public void report(Long me, Long target, String reason) {
        reports.save(Report.builder().reporterId(me).reportedId(target).reason(reason.trim()).build());
    }

    /** Meet safely: share a live spot with one trusted friend for a limited time. */
    @Transactional
    public void startShare(Long me, ShareStart r) {
        if (!access.friends(me, r.trustedId()) || access.blocked(me, r.trustedId())) throw ApiException.forbidden("Pick one of your friends");
        int minutes = Math.max(15, Math.min(r.minutes(), 480));
        LocationShare s = shares.findByOwnerId(me).orElseGet(() -> LocationShare.builder().ownerId(me).build());
        s.setTrustedId(r.trustedId());
        s.setLat(r.lat());
        s.setLng(r.lng());
        s.setExpiresAt(Instant.now().plus(minutes, ChronoUnit.MINUTES));
        s.setUpdatedAt(Instant.now());
        shares.save(s);
    }

    @Transactional
    public void updateShare(Long me, ShareLocation r) {
        shares.findByOwnerId(me).filter(s -> s.getExpiresAt().isAfter(Instant.now())).ifPresent(s -> {
            s.setLat(r.lat()); s.setLng(r.lng()); s.setUpdatedAt(Instant.now()); shares.save(s);
        });
    }

    @Transactional
    public void stopShare(Long me) {
        shares.findByOwnerId(me).ifPresent(shares::delete);
    }

    public Optional<ShareView> myShare(Long me) {
        return shares.findByOwnerId(me).filter(s -> s.getExpiresAt().isAfter(Instant.now()))
                .map(s -> new ShareView(me, "You", s.getLat(), s.getLng(), s.getExpiresAt()));
    }

    /** Shares that friends have started with me. */
    public List<ShareView> sharedWithMe(Long me) {
        return shares.findByTrustedIdAndExpiresAtAfter(me, Instant.now()).stream()
                .map(s -> new ShareView(s.getOwnerId(), profiles.findById(s.getOwnerId()).map(Profile::getDisplayName).orElse("Friend"), s.getLat(), s.getLng(), s.getExpiresAt())).toList();
    }
}
