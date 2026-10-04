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
@RequestMapping("/api/tea")
@RequiredArgsConstructor
public class TeaController {
    private final TeaService tea;

    @GetMapping
    public List<TeaView> feed(@AuthenticationPrincipal Long me, @RequestParam(required = false) String filter) { return tea.feed(me, filter); }

    @PostMapping
    public TeaView post(@AuthenticationPrincipal Long me, @Valid @RequestBody TeaRequest r) { return tea.post(me, r); }

    @PostMapping("/{id}/react")
    public TeaView react(@AuthenticationPrincipal Long me, @PathVariable Long id, @Valid @RequestBody ReactRequest r) { return tea.react(me, id, r.type()); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long me, @PathVariable Long id) { tea.delete(me, id); }
}
