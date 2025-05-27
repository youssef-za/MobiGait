package com.example.mobigait.ui.analysis;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mobigait.R;
import com.example.mobigait.utils.CustomSensorManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class AnalysisFragment extends Fragment implements SensorEventListener {
    private LineChart chart;
    private CustomSensorManager sensorManager;
    private ArrayList<Entry> entries;
    private long startTime;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_analysis, container, false);

        chart = root.findViewById(R.id.realtime_chart);
        entries = new ArrayList<>();
        startTime = System.currentTimeMillis();

        setupChart();
        initializeData();

        sensorManager = new CustomSensorManager(requireContext());
        sensorManager.registerListener(this);

        return root;
    }

    private void setupChart() {
        chart.setDescription(null);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
        chart.animateX(1500);
    }

    private void initializeData() {
        // Add some initial data points
        for (int i = 0; i < 10; i++) {
            entries.add(new Entry(i, 0));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Accelerometer");
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setColor(getResources().getColor(R.color.purple_500));

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float magnitude = (float) Math.sqrt(
                event.values[0] * event.values[0] +
                event.values[1] * event.values[1] +
                event.values[2] * event.values[2]
            );

            float timeInSeconds = (System.currentTimeMillis() - startTime) / 1000f;
            entries.add(new Entry(timeInSeconds, magnitude));

            if (entries.size() > 100) {
                entries.remove(0);
            }

            LineDataSet dataSet = new LineDataSet(entries, "Accelerometer");
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);
            dataSet.setColor(getResources().getColor(R.color.purple_500));

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}