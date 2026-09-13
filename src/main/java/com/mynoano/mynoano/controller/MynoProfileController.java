package com.mynoano.mynoano.controller;

import com.mynoano.mynoano.dto.MynoProfileRequest;
import com.mynoano.mynoano.dto.MynoProfileResponse;
import com.mynoano.mynoano.entity.User;
import com.mynoano.mynoano.repository.UserRepository;
import com.mynoano.mynoano.service.MynoProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/myno/profile")
public class MynoProfileController {

    private final MynoProfileService mynoProfileService;
    private final UserRepository userRepository;

    public MynoProfileController(
            MynoProfileService mynoProfileService,
            UserRepository userRepository
    ) {
        this.mynoProfileService = mynoProfileService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<MynoProfileResponse> createProfile(
            @Valid @RequestBody MynoProfileRequest request,
            Authentication authentication
    ) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MynoProfileResponse response =
                mynoProfileService.createProfile(
                        user.getId(),
                        request
                );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<MynoProfileResponse> getProfile(
            @PathVariable Long userId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MynoProfileResponse response =
                mynoProfileService.getProfile(user);

        return ResponseEntity.ok(response);
    }
}