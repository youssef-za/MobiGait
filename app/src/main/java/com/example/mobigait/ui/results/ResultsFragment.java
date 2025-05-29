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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;

import com.example.mobigait.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResultsFragment extends Fragment {
    private TextView dateText;
    private TextView durationValue;
    private TextView stepCountValue;
    private TextView frequencyText;
    private TextView stepLengthText;
    private TextView speedText;
    private TextView symmetryText;
    private ProgressBar symmetryProgress;
    private CardView emptyStateCard;
    private View mainMetricsLayout;
    private CardView detailedMetricsCard;
    private View actionButtonsLayout;
    private Button shareButton;
    private Button newAnalysisButton;

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

        initializeViews(view);
        setupButtons();
        loadLastAnalysisResults();

        return view;
    }

    private void initializeViews(View view) {
        dateText = view.findViewById(R.id.date_text);
        durationValue = view.findViewById(R.id.duration_value);
        stepCountValue = view.findViewById(R.id.step_count_value);
        frequencyText = view.findViewById(R.id.frequency_text);
        stepLengthText = view.findViewById(R.id.step_length_text);
        speedText = view.findViewById(R.id.speed_text);
        symmetryText = view.findViewById(R.id.symmetry_text);
        symmetryProgress = view.findViewById(R.id.symmetry_progress);
        emptyStateCard = view.findViewById(R.id.empty_state_card);
        mainMetricsLayout = view.findViewById(R.id.main_metrics_layout);
        detailedMetricsCard = view.findViewById(R.id.detailed_metrics_card);
        actionButtonsLayout = view.findViewById(R.id.action_buttons_layout);
        shareButton = view.findViewById(R.id.share_button);
        newAnalysisButton = view.findViewById(R.id.new_analysis_button);
    }

    private void setupButtons() {
        shareButton.setOnClickListener(v -> shareResults());
        newAnalysisButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_analysis);
        });
    }

    private void shareResults() {
        SharedPreferences prefs = requireContext().getSharedPreferences("analysis_results", Context.MODE_PRIVATE);
        long lastAnalysisDate = prefs.getLong("last_analysis_date", 0);

        if (lastAnalysisDate == 0) {
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        long duration = prefs.getLong("last_analysis_duration", 0);
        int stepCount = prefs.getInt("last_step_count", 0);
        float frequency = prefs.getFloat("last_frequency", 0);
        float stepLength = prefs.getFloat("last_step_length", 0);
        float speed = prefs.getFloat("last_speed", 0);
        float symmetry = prefs.getFloat("last_symmetry", 0);

        int minutes = (int) (duration / 60000);
        int seconds = (int) ((duration % 60000) / 1000);

        String shareText = String.format(
            "📊 Résultats MobiGait\n\n" +
            "📅 Date: %s\n" +
            "⏱️ Durée: %02d:%02d\n" +
            "👣 Nombre de pas: %d\n" +
            "🔄 Fréquence: %.2f Hz\n" +
            "📏 Longueur des pas: %.2f m\n" +
            "🏃 Vitesse: %.2f m/s\n" +
            "⚖️ Symétrie: %.1f%%\n\n" +
            "Analysé avec MobiGait 📱",
            dateFormat.format(new Date(lastAnalysisDate)),
            minutes, seconds,
            stepCount,
            frequency,
            stepLength,
            speed,
            symmetry
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Résultats d'analyse MobiGait");

        startActivity(Intent.createChooser(shareIntent, "Partager les résultats"));
    }

    @Override
    public void onResume() {
        super.onResume();
        android.util.Log.d("ResultsFragment", "onResume - registering receiver");

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(analysisResultReceiver,
                new IntentFilter("ANALYSIS_COMPLETED"));

        loadLastAnalysisResults();
    }

    @Override
    public void onPause() {
        super.onPause();
        android.util.Log.d("ResultsFragment", "onPause - unregistering receiver");

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
        emptyStateCard.setVisibility(View.VISIBLE);
        mainMetricsLayout.setVisibility(View.GONE);
        detailedMetricsCard.setVisibility(View.GONE);
        actionButtonsLayout.setVisibility(View.GONE);

        dateText.setText("Aucune analyse disponible");
    }

    private void displayLastResults(SharedPreferences prefs) {
        emptyStateCard.setVisibility(View.GONE);
        mainMetricsLayout.setVisibility(View.VISIBLE);
        detailedMetricsCard.setVisibility(View.VISIBLE);
        actionButtonsLayout.setVisibility(View.VISIBLE);

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

        dateText.setText(dateFormat.format(new Date(lastAnalysisDate)));

        int minutes = (int) (duration / 60000);
        int seconds = (int) ((duration % 60000) / 1000);
        durationValue.setText(String.format("%02d:%02d", minutes, seconds));

        stepCountValue.setText(String.valueOf(stepCount));
        frequencyText.setText(String.format("%.2f Hz", frequency));
        stepLengthText.setText(String.format("%.2f m", stepLength));
        speedText.setText(String.format("%.2f m/s", speed));
        symmetryText.setText(String.format("%.1f%%", symmetry));

        // Animer la barre de progression de symétrie
        symmetryProgress.setProgress(0);
        symmetryProgress.post(() -> {
            android.animation.ObjectAnimator.ofInt(symmetryProgress, "progress", 0, (int) symmetry)
                .setDuration(1000)
                .start();
        });
    }
}
