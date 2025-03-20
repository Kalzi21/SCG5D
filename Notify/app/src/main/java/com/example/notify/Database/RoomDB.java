package com.example.notify.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notify.Models.Notes;

@Database(entities = Notes.class, version = 3, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    public static RoomDB database;
    private static String DATABASE_NAME = "Notify";

    // Modify the getInstance method to accept a Context (not specific to MainActivity)
    public synchronized static RoomDB getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(),
                    RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract MainDAO maindao();
}
