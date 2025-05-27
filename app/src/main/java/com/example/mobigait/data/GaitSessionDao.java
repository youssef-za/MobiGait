package com.example.mobigait.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface GaitSessionDao {
    @Insert
    long insert(GaitSession session);

    @Update
    void update(GaitSession session);

    @Query("SELECT * FROM gait_sessions ORDER BY startTime DESC")
    LiveData<List<GaitSession>> getAllSessions();

    @Query("SELECT * FROM gait_sessions WHERE id = :sessionId")
    LiveData<GaitSession> getSession(long sessionId);

    @Query("SELECT * FROM gait_sessions WHERE id = :sessionId")
    GaitSession getSessionSync(long sessionId);

    @Query("DELETE FROM gait_sessions WHERE id = :sessionId")
    void deleteSession(long sessionId);
} 