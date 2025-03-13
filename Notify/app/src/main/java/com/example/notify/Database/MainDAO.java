package com.example.notify.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.notify.Models.Notes;

import java.util.List;

@Dao
public abstract class MainDAO {

    @Insert(onConflict = REPLACE)
    public void insert(Notes new_notes) {

    }

    @Query("SELECT * FROM notes ORDER BY id DESC")
    public List<Notes> getAll() {

        return null;
    }

    @Query("UPDATE notes SET title = :title, notes = :notes WHERE ID = :id")
    void update(int id, String title, String notes) {

    }

    @Delete
    void delete(Notes notes) {

    }
}
