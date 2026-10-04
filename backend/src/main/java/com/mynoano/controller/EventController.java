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
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService events;

    @GetMapping
    public List<VenueGroup> list(@AuthenticationPrincipal Long me, @RequestParam double lat, @RequestParam double lng,
                                 @RequestParam(defaultValue = "10") double radius) {
        return events.list(me, lat, lng, Math.min(radius, 100));
    }

    @PostMapping
    public EventCard create(@AuthenticationPrincipal Long me, @Valid @RequestBody EventRequest r) { return events.create(me, r); }

    @GetMapping("/{id}")
    public EventDetail detail(@AuthenticationPrincipal Long me, @PathVariable Long id) { return events.detail(me, id); }

    @PostMapping("/{id}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void join(@AuthenticationPrincipal Long me, @PathVariable Long id) { events.join(me, id); }

    @DeleteMapping("/{id}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leave(@AuthenticationPrincipal Long me, @PathVariable Long id) { events.leave(me, id); }

    @PostMapping("/{id}/checkin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkIn(@AuthenticationPrincipal Long me, @PathVariable Long id) { events.checkIn(me, id, true); }

    @DeleteMapping("/{id}/checkin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkOut(@AuthenticationPrincipal Long me, @PathVariable Long id) { events.checkIn(me, id, false); }

    @PostMapping("/{id}/end")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void end(@AuthenticationPrincipal Long me, @PathVariable Long id) { events.end(me, id); }

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PhotoView photo(@AuthenticationPrincipal Long me, @PathVariable Long id, @RequestPart("file") MultipartFile file,
                           @RequestParam(required = false) String caption) {
        return events.addPhoto(me, id, file, caption);
    }

    @GetMapping("/{id}/chat")
    public List<EventChatView> chat(@AuthenticationPrincipal Long me, @PathVariable Long id) { return events.chat(me, id); }

    @PostMapping("/{id}/chat")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void post(@AuthenticationPrincipal Long me, @PathVariable Long id, @Valid @RequestBody EventChatPost r) { events.postChat(me, id, r.body()); }
}
