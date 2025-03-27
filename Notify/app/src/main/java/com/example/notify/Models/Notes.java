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

    @PropertyName("isPinned")
    private boolean isPinned;

    @PropertyName("isFavourite")
    private boolean isFavourite;

    @PropertyName("isArchived")
    private boolean isArchived;
    private String taggedUsers= "";

    private List<String> taggedUsersList = new ArrayList<>();
    private transient List<String> taggedUsernames = new ArrayList<>();

    public Notes() {
        this.title = "";
        this.notes = "";
        this.date = "";
        this.imageUri = "";
        this.tasks = "";
        this.taggedUsers = "";
        this.reminderTime = 0L;
        this.isPinned = false;
        this.isFavourite = false;
        this.isArchived = false;
    }

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

    @PropertyName("isPinned")
    public boolean isPinned() {
        return isPinned;
    }

    @PropertyName("isPinned")
    public void setPinned(boolean pinned) {
        this.isPinned = pinned;
    }

    @PropertyName("isFavourite")
    public boolean isFavourite() {
        return isFavourite;
    }

    @PropertyName("isFavourite")
    public void setFavourite(boolean favourite) {
        this.isFavourite = favourite;
    }

    @PropertyName("isArchived")
    public boolean isArchived() {
        return isArchived;
    }

    @PropertyName("isArchived")
    public void setArchived(boolean archived) {
        this.isArchived = archived;
    }

    // Getter for taggedUsers (Firestore-compatible)
    public String getTaggedUsers() {
        return taggedUsers != null ? taggedUsers : "";
    }

    public void setTaggedUsers(String taggedUsers) {
        this.taggedUsers = taggedUsers != null ? taggedUsers : "";
        // Update the list representation
        if (!this.taggedUsers.isEmpty()) {
            this.taggedUsersList = new ArrayList<>(Arrays.asList(this.taggedUsers.split(",")));
        } else {
            this.taggedUsersList = new ArrayList<>();
        }
    }

    public List<String> getTaggedUsersList() {
        if (taggedUsersList == null) {
            taggedUsersList = new ArrayList<>();
        }
        return taggedUsersList;
    }

    public void setTaggedUsersList(List<String> taggedUsersList) {
        this.taggedUsersList = taggedUsersList != null ? taggedUsersList : new ArrayList<>();
        // Update the taggedUsers string representation
        this.taggedUsers = String.join(",", this.taggedUsersList);
    }

    public List<String> getTaggedUsernames() {
        if (taggedUsernames == null) {
            taggedUsernames = new ArrayList<>();
        }
        return taggedUsernames;
    }

    public void addTaggedUsername(String username) {
        if (taggedUsernames == null) {
            taggedUsernames = new ArrayList<>();
        }
        if (!taggedUsernames.contains(username)) {
            taggedUsernames.add(username);
        }
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