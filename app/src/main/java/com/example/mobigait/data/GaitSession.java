package com.example.mobigait.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gait_sessions")
public class GaitSession {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long startTime;
    private long endTime;
    private float stepFrequency;
    private float averageStepLength;
    private float estimatedSpeed;
    private float symmetryScore;
    private String notes;

    public GaitSession() {
        this.startTime = startTime;
        this.endTime = 0;
        this.stepFrequency = 0;
        this.averageStepLength = 0;
        this.estimatedSpeed = 0;
        this.symmetryScore = 0;
        this.notes = "";
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public float getStepFrequency() { return stepFrequency; }
    public void setStepFrequency(float stepFrequency) { this.stepFrequency = stepFrequency; }
    public float getAverageStepLength() { return averageStepLength; }
    public void setAverageStepLength(float averageStepLength) { this.averageStepLength = averageStepLength; }
    public float getEstimatedSpeed() { return estimatedSpeed; }
    public void setEstimatedSpeed(float estimatedSpeed) { this.estimatedSpeed = estimatedSpeed; }
    public float getSymmetryScore() { return symmetryScore; }
    public void setSymmetryScore(float symmetryScore) { this.symmetryScore = symmetryScore; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}