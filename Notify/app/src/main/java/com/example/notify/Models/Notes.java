package com.example.notify.Models;

import java.io.Serializable;
import java.util.List;

public class Notes implements Serializable {

    private String ID;
    private String userId; // Owner of the note
    private List<String> taggedUsers; // List of tagged user IDs
    private String title;
    private String notes;
    private String date;
    private boolean pinned;
    private boolean isFavourite;
    private boolean isArchived;

    // Empty constructor required for Firebase
    public Notes() {
    }

    // Constructor with tagged users
    public Notes(String ID, String userId, List<String> taggedUsers, String title, String notes, String date, boolean pinned, boolean isFavourite, boolean isArchived) {
        this.ID = ID;
        this.userId = userId;
        this.taggedUsers = taggedUsers;
        this.title = title;
        this.notes = notes;
        this.date = date;
        this.pinned = pinned;
        this.isFavourite = isFavourite;
        this.isArchived = isArchived;
    }

    // Getters and Setters
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getTaggedUsers() {
        return taggedUsers;
    }

    public void setTaggedUsers(List<String> taggedUsers) {
        this.taggedUsers = taggedUsers;
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

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }
}
