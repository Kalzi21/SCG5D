package com.example.notify.Database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notify.MainActivity;
import com.example.notify.Models.Notes;

@Database(entities = Notes.class, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    public static RoomDB database;
    private static String DATABASE_NAME = "Notify";

    public synchronized static  RoomDB getInstance(MainActivity context){
        if (database == null){
            database = Room.databaseBuilder(context.getApplicationContext(),
                    RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public  abstract MainDAO maindao();
}
