package com.example.mobigait.data;

public class AnalysisResult {
    public long date;
    public long duration;
    public int stepCount;
    public float frequency;
    public float stepLength;
    public float speed;
    public float symmetry;

    public AnalysisResult(long date, long duration, int stepCount, float frequency,
                          float stepLength, float speed, float symmetry) {
        this.date = date;
        this.duration = duration;
        this.stepCount = stepCount;
        this.frequency = frequency;
        this.stepLength = stepLength;
        this.speed = speed;
        this.symmetry = symmetry;
    }
}