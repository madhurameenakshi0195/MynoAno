package com.mynoano.mynoano.controller;

import com.mynoano.mynoano.dto.LoginRequest;
import com.mynoano.mynoano.dto.LoginResponse;
import com.mynoano.mynoano.dto.RegisterRequest;
import com.mynoano.mynoano.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

//    @PostMapping("/register")
//    public ResponseEntity<?> register(
//            @Valid @RequestBody RegisterRequest request
//    ) {
//        authService.register(request);
//        return ResponseEntity.ok("Registration successful");
//    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}