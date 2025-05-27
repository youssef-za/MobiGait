package com.example.mobigait.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mobigait.data.GaitData;
import com.example.mobigait.data.GaitDataDao;
import com.example.mobigait.data.GaitSession;
import com.example.mobigait.data.GaitSessionDao;

@Database(entities = {GaitData.class, GaitSession.class}, version = 1)
public abstract class GaitDatabase extends RoomDatabase {
    public abstract GaitDataDao gaitDataDao();
    public abstract GaitSessionDao gaitSessionDao();

    private static volatile GaitDatabase INSTANCE;

    public static GaitDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GaitDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        GaitDatabase.class, "gait_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}