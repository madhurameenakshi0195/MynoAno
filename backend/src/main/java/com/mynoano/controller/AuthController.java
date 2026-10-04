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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void register(@Valid @RequestBody RegisterRequest r) { auth.register(r); }

    @PostMapping("/verify")
    public AuthResponse verify(@Valid @RequestBody VerifyRequest r) { return auth.verify(r); }

    @PostMapping("/resend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resend(@Valid @RequestBody EmailRequest r) { auth.resend(r.email()); }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest r) { return auth.login(r); }
}
