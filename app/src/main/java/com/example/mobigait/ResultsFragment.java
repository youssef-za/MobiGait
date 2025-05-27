package com.example.mobigait;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.mobigait.data.AppDatabase;
import com.example.mobigait.data.GaitSession;
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
    private AppDatabase database;

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

        // Initialize database
        database = AppDatabase.getDatabase(requireContext());

        // Load last session data
        loadLastSession();

        return view;
    }

    private void loadLastSession() {
        new Thread(() -> {
            GaitSession lastSession = database.gaitSessionDao().getSessionSync(1); // Assuming 1 is the last session ID
            if (lastSession != null) {
                requireActivity().runOnUiThread(() -> displaySessionData(lastSession));
            }
        }).start();
    }

    private void displaySessionData(GaitSession session) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        dateText.setText(String.format("Date: %s", dateFormat.format(new Date(session.getStartTime()))));

        long duration = session.getEndTime() - session.getStartTime();
        int minutes = (int) (duration / 60000);
        int seconds = (int) ((duration % 60000) / 1000);
        durationText.setText(String.format("Durée: %02d:%02d", minutes, seconds));

        stepCountText.setText(String.format("Nombre de pas: %.0f", session.getStepFrequency() * (duration / 1000f)));
        frequencyText.setText(String.format("Fréquence: %.2f Hz", session.getStepFrequency()));
        stepLengthText.setText(String.format("Longueur moyenne des pas: %.2f m", session.getAverageStepLength()));
        speedText.setText(String.format("Vitesse estimée: %.2f m/s", session.getEstimatedSpeed()));
        symmetryText.setText(String.format("Symétrie: %.1f%%", session.getSymmetryScore() * 100));
    }
} 