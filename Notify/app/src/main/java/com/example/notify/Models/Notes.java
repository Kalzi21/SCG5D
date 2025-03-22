package com.example.notify.Models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Notes implements Serializable, Parcelable {

    private String ID;
    private String userId; // Owner of the note
    private List<String> taggedUsers; // List of tagged user IDs
    private String title;
    private String notes;
    private Timestamp date; // 🔹 Firestore Timestamp instead of String
    private boolean pinned;
    private boolean isFavourite;
    private boolean isArchived;

    // Empty constructor required for Firebase
    public Notes() {
    }

    // Constructor with Timestamp date
    public Notes(String ID, String userId, List<String> taggedUsers, String title, String notes, Timestamp date, boolean pinned, boolean isFavourite, boolean isArchived) {
        this.ID = ID;
        this.userId = userId;
        this.taggedUsers = taggedUsers;
        this.title = title;
        this.notes = notes;
        this.date = date; // 🔹 Store Firestore Timestamp
        this.pinned = pinned;
        this.isFavourite = isFavourite;
        this.isArchived = isArchived;
    }

    // Parcelable implementation
    protected Notes(Parcel in) {
        ID = in.readString();
        userId = in.readString();
        taggedUsers = new ArrayList<>();
        in.readStringList(taggedUsers);
        title = in.readString();
        notes = in.readString();

        // Handle Timestamp - read as seconds and nanoseconds
        boolean hasDate = in.readByte() != 0;
        if (hasDate) {
            long seconds = in.readLong();
            int nanoseconds = in.readInt();
            date = new Timestamp(seconds, nanoseconds);
        } else {
            date = null;
        }

        pinned = in.readByte() != 0;
        isFavourite = in.readByte() != 0;
        isArchived = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(userId);
        dest.writeStringList(taggedUsers);
        dest.writeString(title);
        dest.writeString(notes);

        // Handle Timestamp - write as seconds and nanoseconds
        if (date != null) {
            dest.writeByte((byte) 1);
            dest.writeLong(date.getSeconds());
            dest.writeInt(date.getNanoseconds());
        } else {
            dest.writeByte((byte) 0);
        }

        dest.writeByte((byte) (pinned ? 1 : 0));
        dest.writeByte((byte) (isFavourite ? 1 : 0));
        dest.writeByte((byte) (isArchived ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Notes> CREATOR = new Creator<Notes>() {
        @Override
        public Notes createFromParcel(Parcel in) {
            return new Notes(in);
        }

        @Override
        public Notes[] newArray(int size) {
            return new Notes[size];
        }
    };

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

    public Timestamp getDate() { // 🔹 Return Timestamp
        return date;
    }

    public void setDate(Timestamp date) { // 🔹 Accept Timestamp
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