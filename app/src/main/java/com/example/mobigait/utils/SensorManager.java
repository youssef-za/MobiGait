package com.example.mobigait.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import java.util.ArrayList;
import java.util.List;

public class SensorManager implements SensorEventListener {
    private static final float STEP_THRESHOLD = 10f;
    private android.hardware.SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private List<SensorDataListener> listeners;
    private boolean isListening;

    public interface SensorDataListener {
        void onSensorDataChanged(float[] accelerometerData, float[] gyroscopeData, long timestamp);
    }

    public SensorManager(Context context) {
        sensorManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        listeners = new ArrayList<>();
        isListening = false;
    }

    public void registerListener(SensorDataListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
        if (!isListening) {
            startListening();
        }
    }

    public void unregisterListener(SensorDataListener listener) {
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            stopListening();
        }
    }

    private void startListening() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
        }
        isListening = true;
    }

    private void stopListening() {
        sensorManager.unregisterListener(this);
        isListening = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (listeners.isEmpty()) return;

        float[] accelerometerData = null;
        float[] gyroscopeData = null;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerData = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeData = event.values.clone();
        }

        for (SensorDataListener listener : listeners) {
            listener.onSensorDataChanged(accelerometerData, gyroscopeData, event.timestamp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this implementation
    }
} 