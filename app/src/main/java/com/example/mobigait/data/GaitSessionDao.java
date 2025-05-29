package com.example.mobigait.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GaitSessionDao {
    @Query("SELECT * FROM gait_sessions")
    LiveData<List<GaitSession>> getAllSessions();

    @Query("SELECT * FROM gait_sessions ORDER BY startTime DESC")
    List<GaitSession> getAllSessionsSync();

    @Query("SELECT * FROM gait_sessions WHERE id = :sessionId")
    LiveData<GaitSession> getSession(long sessionId);

    @Query("SELECT * FROM gait_sessions WHERE id = :sessionId")
    GaitSession getSessionSync(long sessionId);

    @Query("SELECT * FROM gait_sessions ORDER BY startTime DESC LIMIT 1")
    GaitSession getLastSessionSync();

    @Insert
    long insert(GaitSession session);

    @Update
    void update(GaitSession session);

    @Delete
    void delete(GaitSession session);

    @Query("DELETE FROM gait_sessions WHERE id = :sessionId")
    void deleteById(long sessionId);

    @Query("DELETE FROM gait_sessions WHERE id IN (:sessionIds)")
    void deleteByIds(List<Long> sessionIds);

    @Query("DELETE FROM gait_sessions")
    void deleteAll();
}