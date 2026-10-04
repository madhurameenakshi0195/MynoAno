package com.mynoano.controller;

import com.mynoano.dto.Dtos.*;
import com.mynoano.entity.*;
import com.mynoano.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profiles;
    private final DiscoveryService discovery;
    private final FriendService friends;
    private final TeaService tea;
    private final EventService events;

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal Long me) { return profiles.me(me); }

    @PutMapping("/me/profile")
    public MeResponse save(@AuthenticationPrincipal Long me, @Valid @RequestBody ProfileRequest r) { return profiles.save(me, r); }

    @PutMapping("/me/intent")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void intent(@AuthenticationPrincipal Long me, @RequestBody IntentRequest r) { profiles.intent(me, r.intent()); }

    @PostMapping("/me/selfie-verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void selfie(@AuthenticationPrincipal Long me) { profiles.selfieVerified(me); }

    @PutMapping("/me/discovery")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void discovery(@AuthenticationPrincipal Long me, @RequestBody DiscoveryRequest r) { discovery.settings(me, r); }

    @PutMapping("/me/location")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void location(@AuthenticationPrincipal Long me, @Valid @RequestBody LocationRequest r) { discovery.location(me, r); }

    /** The private ANO page: friends with circle status, my ANO events and ANO tea. */
    @GetMapping("/me/ano")
    public AnoHome ano(@AuthenticationPrincipal Long me) {
        return new AnoHome(friends.friends(me), tea.mine(me, TeaSource.ANO), events.hosted(me, Visibility.ANO));
    }

    @GetMapping("/people/{id}")
    public PersonProfile person(@AuthenticationPrincipal Long me, @PathVariable Long id) { return profiles.view(me, id); }
}
