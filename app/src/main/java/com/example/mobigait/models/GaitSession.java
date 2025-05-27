package com.example.mobigait.models;

import androidx.room.PrimaryKey;

import java.util.Date;

public class GaitSession {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private Date date;
    private long duration;
    private float stepFrequency;
    private float averageSpeed;
    private float stepLength;
    private float symmetryIndex;

    // Constructor, getters, setters
}