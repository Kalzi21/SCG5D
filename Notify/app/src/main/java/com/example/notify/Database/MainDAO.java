package com.example.notify.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

import android.util.Log;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notify.Models.Notes;

import java.util.List;

@Dao
public abstract class MainDAO {

    // Insert a new note
    @Insert(onConflict = REPLACE)
    public abstract void insert(Notes new_notes);

    // Get all notes
    @Query("SELECT * FROM notes ORDER BY pinned DESC, id DESC")
    public abstract List<Notes> getAll();

    // Update a note by its ID
    @Query("UPDATE notes SET title = :title, notes = :notes WHERE ID = :id")
    public abstract void update(int id, String title, String notes);

    // Delete a note
    @Delete
    public abstract void delete(Notes notes);

    // Get all favourite notes (is_favourite = 1)
    @Query("SELECT * FROM notes WHERE is_favourite = 1 ORDER BY id DESC")
    public abstract List<Notes> getFavourites();

    // Get all archived notes (is_archived = 1)
    @Query("SELECT * FROM notes WHERE is_archived = 1 ORDER BY id DESC")
    public abstract List<Notes> getArchived();

    @Update
    public abstract void update(Notes notes);

    @Query("UPDATE notes SET pinned = :pinned WHERE ID = :id")
    public abstract void pin(int id, boolean pinned);


    @Query("UPDATE notes SET pinned = 0")
    public abstract void unpinAll();
    // Add these methods for favourites/archived
    @Query("UPDATE notes SET is_favourite = :favourite WHERE ID = :id")
    public abstract void setFavourite(int id, boolean favourite);

    @Query("UPDATE notes SET is_archived = :archived WHERE ID = :id")
    public abstract void setArchived(int id, boolean archived);

    // Optional: You can add methods to handle pinning or filtering by other criteria if needed.
}