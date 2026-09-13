package com.mynoano.mynoano.controller;


import com.mynoano.mynoano.dto.UserProfileResponse;
import com.mynoano.mynoano.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(
            UserProfileService userProfileService
    ) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfile(
            @PathVariable Long id
    ) {

        UserProfileResponse response =
                userProfileService.getProfile(id);

        return ResponseEntity.ok(response);
    }
}