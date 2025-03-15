package com.example.notify.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

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
    @Query("SELECT * FROM notes ORDER BY id DESC")
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

    // Optional: You can add methods to handle pinning or filtering by other criteria if needed.
}
