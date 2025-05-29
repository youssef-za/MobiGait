package com.example.mobigait.ui.results;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mobigait.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResultsFragment extends Fragment {
    private TextView dateText;
    private TextView durationText;
    private TextView stepCountText;
    private TextView frequencyText;
    private TextView stepLengthText;
    private TextView speedText;
    private TextView symmetryText;

    private BroadcastReceiver analysisResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            android.util.Log.d("ResultsFragment", "Received broadcast - updating results");
            loadLastAnalysisResults();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        // Initialize views
        dateText = view.findViewById(R.id.date_text);
        durationText = view.findViewById(R.id.duration_text);
        stepCountText = view.findViewById(R.id.step_count_text);
        frequencyText = view.findViewById(R.id.frequency_text);
        stepLengthText = view.findViewById(R.id.step_length_text);
        speedText = view.findViewById(R.id.speed_text);
        symmetryText = view.findViewById(R.id.symmetry_text);

        // Load and display last analysis results
        loadLastAnalysisResults();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        android.util.Log.d("ResultsFragment", "onResume - registering receiver");

        // S'enregistrer pour recevoir les mises à jour
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(analysisResultReceiver,
                new IntentFilter("ANALYSIS_COMPLETED"));

        // Recharger les résultats au cas où ils auraient changé
        loadLastAnalysisResults();
    }

    @Override
    public void onPause() {
        super.onPause();
        android.util.Log.d("ResultsFragment", "onPause - unregistering receiver");

        // Se désinscrire des mises à jour
        try {
            LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(analysisResultReceiver);
        } catch (Exception e) {
            android.util.Log.e("ResultsFragment", "Error unregistering receiver", e);
        }
    }

    private void loadLastAnalysisResults() {
        SharedPreferences prefs = requireContext().getSharedPreferences("analysis_results", Context.MODE_PRIVATE);

        long lastAnalysisDate = prefs.getLong("last_analysis_date", 0);

        android.util.Log.d("ResultsFragment", "Loading results, date: " + lastAnalysisDate);

        if (lastAnalysisDate == 0) {
            displayNoResults();
        } else {
            displayLastResults(prefs);
        }
    }

    private void displayNoResults() {
        dateText.setText("Date: Aucune analyse effectuée");
        durationText.setText("Durée: --");
        stepCountText.setText("Nombre de pas: --");
        frequencyText.setText("Fréquence: --");
        stepLengthText.setText("Longueur moyenne des pas: --");
        speedText.setText("Vitesse estimée: --");
        symmetryText.setText("Symétrie: --");
    }

    private void displayLastResults(SharedPreferences prefs) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        long lastAnalysisDate = prefs.getLong("last_analysis_date", 0);
        long duration = prefs.getLong("last_analysis_duration", 0);
        int stepCount = prefs.getInt("last_step_count", 0);
        float frequency = prefs.getFloat("last_frequency", 0);
        float stepLength = prefs.getFloat("last_step_length", 0);
        float speed = prefs.getFloat("last_speed", 0);
        float symmetry = prefs.getFloat("last_symmetry", 0);

        android.util.Log.d("ResultsFragment", "Displaying results: steps=" + stepCount +
            ", frequency=" + frequency + ", duration=" + duration);

        dateText.setText(String.format("Date: %s", dateFormat.format(new Date(lastAnalysisDate))));

        int minutes = (int) (duration / 60000);
        int seconds = (int) ((duration % 60000) / 1000);
        durationText.setText(String.format("Durée: %02d:%02d", minutes, seconds));

        stepCountText.setText(String.format("Nombre de pas: %d", stepCount));
        frequencyText.setText(String.format("Fréquence: %.2f Hz", frequency));
        stepLengthText.setText(String.format("Longueur moyenne des pas: %.2f m", stepLength));
        speedText.setText(String.format("Vitesse estimée: %.2f m/s", speed));
        symmetryText.setText(String.format("Symétrie: %.1f%%", symmetry));
    }
}
