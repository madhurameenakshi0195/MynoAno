package com.mynoano.mynoano.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "myno_profiles")
public class MynoProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String displayName;

    @Column(length = 500)
    private String bio;

    private String profilePicture;

    // Constructors

    public MynoProfile() {
    }

    public MynoProfile(User user, String displayName, String bio, String profilePicture) {
        this.user = user;
        this.displayName = displayName;
        this.bio = bio;
        this.profilePicture = profilePicture;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}