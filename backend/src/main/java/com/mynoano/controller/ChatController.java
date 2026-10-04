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
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chat;

    @GetMapping
    public List<ConversationView> conversations(@AuthenticationPrincipal Long me) { return chat.conversations(me); }

    @GetMapping("/{userId}")
    public List<MessageView> thread(@AuthenticationPrincipal Long me, @PathVariable Long userId) { return chat.thread(me, userId); }

    @PostMapping("/{userId}")
    public MessageView send(@AuthenticationPrincipal Long me, @PathVariable Long userId, @Valid @RequestBody SendMessage r) { return chat.send(me, userId, r); }
}
