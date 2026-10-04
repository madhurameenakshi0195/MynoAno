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
public class FriendController {
    private final FriendService friends;

    @GetMapping("/friends")
    public List<FriendView> list(@AuthenticationPrincipal Long me) { return friends.friends(me); }

    @GetMapping("/friends/requests")
    public List<FriendView> incoming(@AuthenticationPrincipal Long me) { return friends.incoming(me); }

    @PostMapping("/friends/{id}/request")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void request(@AuthenticationPrincipal Long me, @PathVariable Long id) { friends.request(me, id); }

    @PostMapping("/friends/{id}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accept(@AuthenticationPrincipal Long me, @PathVariable Long id) { friends.accept(me, id); }

    @DeleteMapping("/friends/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@AuthenticationPrincipal Long me, @PathVariable Long id) { friends.remove(me, id); }

    @PostMapping("/friends/{id}/ano/request")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anoRequest(@AuthenticationPrincipal Long me, @PathVariable Long id) { friends.requestAno(me, id); }

    @PostMapping("/friends/{id}/ano/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anoAccept(@AuthenticationPrincipal Long me, @PathVariable Long id) { friends.acceptAno(me, id); }

    @DeleteMapping("/friends/{id}/ano")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anoLeave(@AuthenticationPrincipal Long me, @PathVariable Long id) { friends.leaveAno(me, id); }

    @PostMapping("/people/{id}/wave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void wave(@AuthenticationPrincipal Long me, @PathVariable Long id, @Valid @RequestBody WaveRequest r) { friends.wave(me, id, r.text()); }

    @GetMapping("/waves")
    public List<WaveView> waves(@AuthenticationPrincipal Long me) { return friends.waves(me); }
}
