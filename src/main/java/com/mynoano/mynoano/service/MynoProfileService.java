package com.mynoano.mynoano.service;

import com.mynoano.mynoano.dto.MynoProfileRequest;
import com.mynoano.mynoano.dto.MynoProfileResponse;
import com.mynoano.mynoano.entity.MynoProfile;
import com.mynoano.mynoano.entity.User;
import com.mynoano.mynoano.repository.MynoProfileRepository;
import com.mynoano.mynoano.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MynoProfileService {

    private final MynoProfileRepository mynoProfileRepository;
    private final UserRepository userRepository;

    public MynoProfileService(
            MynoProfileRepository mynoProfileRepository,
            UserRepository userRepository
    ) {
        this.mynoProfileRepository = mynoProfileRepository;
        this.userRepository = userRepository;
    }

    public MynoProfileResponse getProfile(User user) {

        MynoProfile profile = mynoProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("MYNO profile not found"));

        return convertToResponse(profile);
    }

    public MynoProfileResponse createProfile(
            Long userId,
            MynoProfileRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (mynoProfileRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("MYNO profile already exists");
        }

        MynoProfile profile = new MynoProfile();

        profile.setUser(user);
        profile.setDisplayName(request.getDisplayName());
        profile.setBio(request.getBio());
        profile.setProfilePicture(request.getProfilePicture());

        MynoProfile savedProfile =
                mynoProfileRepository.save(profile);

        return convertToResponse(savedProfile);
    }

    private MynoProfileResponse convertToResponse(
            MynoProfile profile
    ) {

        return new MynoProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getProfilePicture()
        );
    }
}