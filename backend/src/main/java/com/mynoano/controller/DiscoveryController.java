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
public class DiscoveryController {
    private final DiscoveryService discovery;
    private final InviteService invites;

    @GetMapping("/nearby")
    public List<PersonCard> nearby(@AuthenticationPrincipal Long me) { return discovery.nearby(me); }

    @GetMapping("/invites")
    public List<InviteView> invites(@AuthenticationPrincipal Long me) { return invites.list(me); }

    @PostMapping("/invites")
    public InviteView create(@AuthenticationPrincipal Long me, @Valid @RequestBody InviteRequest r) { return invites.create(me, r); }

    @PostMapping("/invites/{id}/join")
    public InviteView join(@AuthenticationPrincipal Long me, @PathVariable Long id) { return invites.toggleJoin(me, id); }

    @DeleteMapping("/invites/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long me, @PathVariable Long id) { invites.delete(me, id); }
}
