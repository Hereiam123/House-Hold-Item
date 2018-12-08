package com.briandemaio.dogwalktimer;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Doggy.class}, version = 1, exportSchema = false)
public abstract class DoggyRoomDatabase extends RoomDatabase {
    public abstract DoggyDao doggyDao();
    private static DoggyRoomDatabase INSTANCE;

    public static DoggyRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DoggyRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DoggyRoomDatabase.class, "doggy_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
