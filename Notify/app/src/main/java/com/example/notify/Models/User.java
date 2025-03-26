package com.example.notify.Models;

public class User {
    private String userId;
    private String email;
    private String username;
    private String profilePictureUrl;

    // Empty constructor required for Firebase
    public User() {
    }

    // Constructor with parameters
    public User(String userId, String email, String username, String profilePictureUrl) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                '}';
    }
}