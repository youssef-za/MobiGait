package com.example.mobigait.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "gait_data",
        foreignKeys = @ForeignKey(entity = GaitSession.class,
                                parentColumns = "id",
                                childColumns = "sessionId",
                                onDelete = ForeignKey.CASCADE))
public class GaitData {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long sessionId;
    private long timestamp;
    private float accX;
    private float accY;
    private float accZ;
    private float gyroX;
    private float gyroY;
    private float gyroZ;

    public GaitData(long sessionId, long timestamp, float accX, float accY, float accZ,
                   float gyroX, float gyroY, float gyroZ) {
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
        this.gyroX = gyroX;
        this.gyroY = gyroY;
        this.gyroZ = gyroZ;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public float getAccX() { return accX; }
    public void setAccX(float accX) { this.accX = accX; }
    public float getAccY() { return accY; }
    public void setAccY(float accY) { this.accY = accY; }
    public float getAccZ() { return accZ; }
    public void setAccZ(float accZ) { this.accZ = accZ; }
    public float getGyroX() { return gyroX; }
    public void setGyroX(float gyroX) { this.gyroX = gyroX; }
    public float getGyroY() { return gyroY; }
    public void setGyroY(float gyroY) { this.gyroY = gyroY; }
    public float getGyroZ() { return gyroZ; }
    public void setGyroZ(float gyroZ) { this.gyroZ = gyroZ; }
} 