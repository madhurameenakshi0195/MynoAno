package com.mynoano.service;

import com.mynoano.dto.Dtos.*;
import com.mynoano.entity.*;
import com.mynoano.exception.ApiException;
import com.mynoano.repository.*;
import com.mynoano.util.Geo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.*;

@Component
@RequiredArgsConstructor
public class CleanupJob {
    private final TeaReactionRepository reactions;
    private final TeaPostRepository teaPosts;
    private final InviteJoinRepository joins;
    private final InviteRepository invites;
    private final LocationShareRepository shares;
    private final ProfileRepository profiles;

    /** Expired tea, invites and location shares are deleted. The ghost-mode timer switches discovery off. */
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void run() {
        Instant now = Instant.now();
        reactions.deleteExpired(now);
        teaPosts.deleteExpired(now);
        joins.deleteExpired(now);
        invites.deleteExpired(now);
        shares.deleteExpired(now);
        profiles.findByDiscoveryOnTrueAndDiscoveryOffAtBefore(now).forEach(p -> {
            p.setDiscoveryOn(false); p.setLat(null); p.setLng(null); p.setDiscoveryOffAt(null); profiles.save(p);
        });
    }
}
