package com.mynoano.mynoano.dto;

public class MynoProfileResponse {

    private Long id;
    private Long userId;
    private String displayName;
    private String bio;
    private String profilePicture;

    public MynoProfileResponse() {
    }

    public MynoProfileResponse(
            Long id,
            Long userId,
            String displayName,
            String bio,
            String profilePicture
    ) {
        this.id = id;
        this.userId = userId;
        this.displayName = displayName;
        this.bio = bio;
        this.profilePicture = profilePicture;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBio() {
        return bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}