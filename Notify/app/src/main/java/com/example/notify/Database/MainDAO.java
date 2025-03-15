package com.example.notify.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

import android.util.Log;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notify.Models.Notes;

import org.json.JSONArray;

import java.util.List;

@Dao
public interface MainDAO {

    // Insert a new note
    @Insert(onConflict = REPLACE)

     void insert(Notes new_notes);


    // Get all notes
    @Query("SELECT * FROM notes ORDER BY id DESC")
     List<Notes> getAll();

    @Query("UPDATE notes SET pinned = :pinned WHERE ID = :id")
     void pin(int id, boolean pinned);


    @Update
     void update(Notes notes); // Replace existing update() method


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

     void delete(Notes notes); // Replace existing delete() method

}
