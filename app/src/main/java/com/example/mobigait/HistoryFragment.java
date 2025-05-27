package com.example.mobigait;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobigait.data.AppDatabase;
import com.example.mobigait.data.GaitSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private SessionAdapter adapter;
    private AppDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.sessions_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SessionAdapter();
        recyclerView.setAdapter(adapter);

        database = AppDatabase.getDatabase(requireContext());
        loadSessions();

        return view;
    }

    private void loadSessions() {
        new Thread(() -> {
            List<GaitSession> sessions = database.gaitSessionDao().getAllSessions().getValue();
            if (sessions != null) {
                requireActivity().runOnUiThread(() -> adapter.setSessions(sessions));
            }
        }).start();
    }

    private class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
        private List<GaitSession> sessions = new ArrayList<>();
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        public void setSessions(List<GaitSession> sessions) {
            this.sessions = sessions;
            notifyDataSetChanged();
        }

        @Override
        public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_session, parent, false);
            return new SessionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SessionViewHolder holder, int position) {
            GaitSession session = sessions.get(position);
            holder.bind(session);
        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }

        class SessionViewHolder extends RecyclerView.ViewHolder {
            private TextView dateText;
            private TextView durationText;
            private TextView stepCountText;

            SessionViewHolder(View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.date_text);
                durationText = itemView.findViewById(R.id.duration_text);
                stepCountText = itemView.findViewById(R.id.step_count_text);
            }

            void bind(GaitSession session) {
                dateText.setText(dateFormat.format(new Date(session.getStartTime())));
                
                long duration = session.getEndTime() - session.getStartTime();
                int minutes = (int) (duration / 60000);
                int seconds = (int) ((duration % 60000) / 1000);
                durationText.setText(String.format("Durée: %02d:%02d", minutes, seconds));
                
                float stepCount = session.getStepFrequency() * (duration / 1000f);
                stepCountText.setText(String.format("Pas: %.0f", stepCount));
            }
        }
    }
} 