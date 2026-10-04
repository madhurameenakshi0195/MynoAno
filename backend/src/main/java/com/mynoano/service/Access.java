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
public class Access {
    private final BlockRepository blocks;
    private final FriendshipRepository friendRepo;

    public boolean blocked(Long a, Long b) { return blocks.either(a, b); }

    /** Everyone who blocked me or whom I blocked. Blocking is two-way. */
    public Set<Long> blockedIds(Long me) {
        Set<Long> s = new HashSet<>();
        blocks.involving(me).forEach(b -> s.add(b.getBlockerId().equals(me) ? b.getBlockedId() : b.getBlockerId()));
        return s;
    }

    public boolean friends(Long a, Long b) {
        return friendRepo.between(a, b).map(f -> f.getStatus() == FriendStatus.ACCEPTED).orElse(false);
    }

    public boolean anoCircle(Long a, Long b) {
        return friendRepo.between(a, b).map(f -> f.getStatus() == FriendStatus.ACCEPTED && f.getAnoStatus() == AnoStatus.ACCEPTED).orElse(false);
    }

    public Set<Long> friendIds(Long me) {
        Set<Long> s = new HashSet<>();
        friendRepo.friendsOf(me).forEach(f -> s.add(f.getRequesterId().equals(me) ? f.getAddresseeId() : f.getRequesterId()));
        return s;
    }

    public Set<Long> anoIds(Long me) {
        Set<Long> s = new HashSet<>();
        friendRepo.friendsOf(me).stream().filter(f -> f.getAnoStatus() == AnoStatus.ACCEPTED)
                .forEach(f -> s.add(f.getRequesterId().equals(me) ? f.getAddresseeId() : f.getRequesterId()));
        return s;
    }
}
