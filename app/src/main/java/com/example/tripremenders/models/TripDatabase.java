package com.example.tripremenders.models;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {TripModel.class, NoteModel.class}, version = 3, exportSchema = false)
public abstract class TripDatabase extends RoomDatabase {


    public abstract TripDao tripDao();

    public abstract NoteDao noteDao();

    private static TripDatabase INSTANCE;

    public static TripDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TripDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                           TripDatabase.class, "trip_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
