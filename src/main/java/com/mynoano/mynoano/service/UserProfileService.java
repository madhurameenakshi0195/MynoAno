package com.mynoano.mynoano.service;

import com.mynoano.mynoano.entity.User;
import com.mynoano.mynoano.dto.UserProfileResponse;
import com.mynoano.mynoano.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    private final UserRepository userRepository;

    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse getProfile(Long userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}