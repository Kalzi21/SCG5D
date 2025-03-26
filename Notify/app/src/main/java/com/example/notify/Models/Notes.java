package com.example.notify.Models;

import com.example.notify.Models.TaggedUsersConverter;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Notes implements Serializable {
    private String title;
    private String notes;
    private String date;
    private String imageUri;
    private long reminderTime;
    private String tasks;

    private boolean isPinned;

    private boolean isFavourite;

    private boolean isArchived;
    private String taggedUsers= "";

    // Getters and Setters

    @Exclude // Exclude from Firestore serialization
    private String documentId; // Firestore document ID


    private String userId;  // Add this field

    // Add getter and setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getTasks() {
        return tasks;
    }

    public void setTasks(String tasks) {
        this.tasks = tasks;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        this.isPinned = pinned;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        this.isFavourite = favourite;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        this.isArchived = archived;
    }


    // Getter for taggedUsers (Firestore-compatible)
    public String getTaggedUsers() {
        return taggedUsers != null ? taggedUsers : "";
    }

    // Setter for taggedUsers (Firestore-compatible)

    // Helper method to get taggedUsers as a List<String>
    @Exclude // Exclude from Firestore serialization
    public List<String> getTaggedUsersList() {
        String users = getTaggedUsers(); // Use the safe getter
        if (users.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(users.split(","));
    }

    // Helper method to set taggedUsers from a List<String>
    @Exclude // Exclude from Firestore serialization
    public void setTaggedUsersList(List<String> taggedUsers) {
        this.taggedUsers = (taggedUsers == null || taggedUsers.isEmpty())
                ? ""
                : String.join(",", taggedUsers);
    }

    // Getter for documentId
    @Exclude // Exclude from Firestore serialization
    public String getDocumentId() {
        return documentId;
    }

    // Setter for documentId
    @Exclude // Exclude from Firestore serialization
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}