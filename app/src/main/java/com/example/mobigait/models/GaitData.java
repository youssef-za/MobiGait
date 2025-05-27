package com.example.mobigait.models;

import androidx.room.PrimaryKey;

public class GaitData {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long timestamp;
    private float accX, accY, accZ;
    private float gyroX, gyroY, gyroZ;
    private long sessionId;

    // Constructor, getters, setters
}