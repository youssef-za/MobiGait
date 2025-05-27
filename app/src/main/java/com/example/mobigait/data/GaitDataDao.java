package com.example.mobigait.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface GaitDataDao {
    @Insert
    void insert(GaitData gaitData);

    @Insert
    void insertAll(List<GaitData> gaitDataList);

    @Query("SELECT * FROM gait_data WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    LiveData<List<GaitData>> getGaitDataForSession(long sessionId);

    @Query("SELECT * FROM gait_data WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    List<GaitData> getGaitDataForSessionSync(long sessionId);

    @Query("DELETE FROM gait_data WHERE sessionId = :sessionId")
    void deleteGaitDataForSession(long sessionId);
} 