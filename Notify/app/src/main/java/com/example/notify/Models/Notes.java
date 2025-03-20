package com.example.notify.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Notes implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int ID;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "pinned")
    private boolean pinned; // For pinning notes

    @ColumnInfo(name = "is_favourite")
    private boolean isFavourite; // For Favourites

    @ColumnInfo(name = "is_archived")
    private boolean isArchived; // For Archived

    @ColumnInfo(name = "image_uri")
    private String imageUri; // Store the URI of the image

    @ColumnInfo(name = "reminder_time")
    private long reminderTime; // Store the reminder time in milliseconds

    @ColumnInfo(name = "tasks")
    private String tasks; // Store tasks as a JSON string or a serialized list

    // Getters and setters for the new fields
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
    // Getters and setters for the fields

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean isArchived) {
        this.isArchived = isArchived;
    }
}
