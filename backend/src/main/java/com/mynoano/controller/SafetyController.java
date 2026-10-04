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
@RequestMapping("/api/safety")
@RequiredArgsConstructor
public class SafetyController {
    private final SafetyService safety;

    @PostMapping("/block/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void block(@AuthenticationPrincipal Long me, @PathVariable Long id) { safety.block(me, id); }

    @DeleteMapping("/block/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(@AuthenticationPrincipal Long me, @PathVariable Long id) { safety.unblock(me, id); }

    @GetMapping("/blocked")
    public List<BlockedView> blocked(@AuthenticationPrincipal Long me) { return safety.blocked(me); }

    @PostMapping("/report/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void report(@AuthenticationPrincipal Long me, @PathVariable Long id, @Valid @RequestBody ReportRequest r) { safety.report(me, id, r.reason()); }

    @PostMapping("/share")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void start(@AuthenticationPrincipal Long me, @Valid @RequestBody ShareStart r) { safety.startShare(me, r); }

    @PutMapping("/share/location")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@AuthenticationPrincipal Long me, @Valid @RequestBody ShareLocation r) { safety.updateShare(me, r); }

    @DeleteMapping("/share")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void stop(@AuthenticationPrincipal Long me) { safety.stopShare(me); }

    @GetMapping("/share")
    public Optional<ShareView> mine(@AuthenticationPrincipal Long me) { return safety.myShare(me); }

    @GetMapping("/shared-with-me")
    public List<ShareView> sharedWithMe(@AuthenticationPrincipal Long me) { return safety.sharedWithMe(me); }
}
