package com.example.mobigait.utils;

import java.util.ArrayList;
import java.util.List;

public class MathUtils {
    private static final float GRAVITY = 9.81f;
    private static final float STEP_LENGTH_CONSTANT = 0.413f; // Constant for step length calculation

    public static float calculateStepFrequency(List<Long> stepTimestamps) {
        if (stepTimestamps.size() < 2) return 0;
        
        long totalTime = stepTimestamps.get(stepTimestamps.size() - 1) - stepTimestamps.get(0);
        float timeInSeconds = totalTime / 1000f; // Convert to seconds
        return (stepTimestamps.size() - 1) / timeInSeconds;
    }

    public static float calculateStepLength(float stepFrequency, float height) {
        // Using a simplified model based on frequency and height
        return height * STEP_LENGTH_CONSTANT * stepFrequency;
    }

    public static float calculateSpeed(float stepLength, float stepFrequency) {
        // Speed = step length * step frequency
        return stepLength * stepFrequency;
    }

    public static float calculateSymmetry(List<Float> leftStepTimes, List<Float> rightStepTimes) {
        if (leftStepTimes.isEmpty() || rightStepTimes.isEmpty()) return 0;

        float leftAvg = calculateAverage(leftStepTimes);
        float rightAvg = calculateAverage(rightStepTimes);
        
        // Symmetry score: 1.0 means perfect symmetry, 0.0 means complete asymmetry
        float difference = Math.abs(leftAvg - rightAvg);
        float maxTime = Math.max(leftAvg, rightAvg);
        return 1.0f - (difference / maxTime);
    }

    public static List<Float> detectSteps(List<Float> accelerometerMagnitudes, float threshold) {
        List<Float> stepTimes = new ArrayList<>();
        boolean wasAboveThreshold = false;

        for (int i = 0; i < accelerometerMagnitudes.size(); i++) {
            float magnitude = accelerometerMagnitudes.get(i);
            
            if (magnitude > threshold && !wasAboveThreshold) {
                stepTimes.add((float) i);
                wasAboveThreshold = true;
            } else if (magnitude <= threshold) {
                wasAboveThreshold = false;
            }
        }

        return stepTimes;
    }

    private static float calculateAverage(List<Float> values) {
        if (values.isEmpty()) return 0;
        float sum = 0;
        for (float value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    public static float calculateAccelerometerMagnitude(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z) - GRAVITY;
    }
} 