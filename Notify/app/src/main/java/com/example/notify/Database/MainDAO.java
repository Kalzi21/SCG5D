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

    @Insert(onConflict = REPLACE)
     void insert(Notes new_notes);

    @Query("SELECT * FROM notes ORDER BY id DESC")
     List<Notes> getAll();

    @Query("UPDATE notes SET pinned = :pinned WHERE ID = :id")
     void pin(int id, boolean pinned);


    @Update
     void update(Notes notes); // Replace existing update() method

    @Delete
     void delete(Notes notes); // Replace existing delete() method
}
