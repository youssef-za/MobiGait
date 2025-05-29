package com.example.mobigait.ui.history;

import android.app.AlertDialog;
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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobigait.R;
import com.example.mobigait.data.AnalysisResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<AnalysisResult> historyList;
    private Button selectAllButton;
    private Button deleteSelectedButton;
    private Button cancelSelectionButton;
    private LinearLayout actionButtonsLayout;
    private TextView emptyMessage;
    private boolean isSelectionMode = false;

    private BroadcastReceiver analysisResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            android.util.Log.d("HistoryFragment", "Received broadcast - reloading history");
            loadHistory();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.sessions_recycler_view);
        selectAllButton = view.findViewById(R.id.select_all_button);
        deleteSelectedButton = view.findViewById(R.id.delete_selected_button);
        cancelSelectionButton = view.findViewById(R.id.cancel_selection_button);
        actionButtonsLayout = view.findViewById(R.id.action_buttons_layout);
        emptyMessage = view.findViewById(R.id.empty_message);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);

        setupButtons();
        removeDuplicates(); // Nettoyer les doublons existants
        loadHistory();

        return view;
    }

    private void setupButtons() {
        selectAllButton.setOnClickListener(v -> {
            boolean allSelected = adapter.areAllSelected();
            adapter.selectAll(!allSelected);
            updateButtonStates();
        });

        deleteSelectedButton.setOnClickListener(v -> {
            List<AnalysisResult> selectedItems = adapter.getSelectedItems();
            if (!selectedItems.isEmpty()) {
                showDeleteConfirmationDialog(selectedItems);
            }
        });
        cancelSelectionButton.setOnClickListener(v -> exitSelectionMode());
    }

    private void enterSelectionMode() {
        isSelectionMode = true;
        actionButtonsLayout.setVisibility(View.VISIBLE);
        adapter.setSelectionMode(true);
        updateButtonStates();
    }

    private void exitSelectionMode() {
        isSelectionMode = false;
        actionButtonsLayout.setVisibility(View.GONE);
        adapter.setSelectionMode(false);
        adapter.clearSelection();
    }

    private void showDeleteConfirmationDialog(List<AnalysisResult> selectedItems) {
        String message = selectedItems.size() == 1 ?
                "Êtes-vous sûr de vouloir supprimer cette analyse ?" :
                "Êtes-vous sûr de vouloir supprimer " + selectedItems.size() + " analyses ?";

        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmer la suppression")
                .setMessage(message)
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    deleteSelectedItems(selectedItems);
                    exitSelectionMode();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void deleteSelectedItems(List<AnalysisResult> selectedItems) {
        historyList.removeAll(selectedItems);
        saveHistoryToPreferences();
        adapter.updateData(historyList);
        updateEmptyState();
    }

    private void deleteItem(AnalysisResult item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmer la suppression")
                .setMessage("Êtes-vous sûr de vouloir supprimer cette analyse ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    historyList.remove(item);
                    saveHistoryToPreferences();
                    adapter.updateData(historyList);
                    updateEmptyState();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void saveHistoryToPreferences() {
        SharedPreferences historyPrefs = requireContext().getSharedPreferences("analysis_history", Context.MODE_PRIVATE);
        historyPrefs.edit().clear().apply();
        Gson gson = new Gson();
        String updatedJson = gson.toJson(historyList);
        historyPrefs.edit().putString("history_list", updatedJson).apply();
        android.util.Log.d("HistoryFragment", "History saved after deletion");
    }

    private void updateButtonStates() {
        int selectedCount = adapter.getSelectedCount();
        deleteSelectedButton.setEnabled(selectedCount > 0);
        deleteSelectedButton.setText(selectedCount > 0 ?
                "Supprimer (" + selectedCount + ")" : "Supprimer");

        selectAllButton.setText(adapter.areAllSelected() ?
                "Tout désélectionner" : "Tout sélectionner");
    }

    private void updateEmptyState() {
        if (historyList.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            actionButtonsLayout.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        android.util.Log.d("HistoryFragment", "onResume - registering receiver");
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(analysisResultReceiver,
                        new IntentFilter("ANALYSIS_COMPLETED"));
        loadHistory();
    }

    @Override
    public void onPause() {
        super.onPause();
        android.util.Log.d("HistoryFragment", "onPause - unregistering receiver");
        try {
            LocalBroadcastManager.getInstance(requireContext())
                    .unregisterReceiver(analysisResultReceiver);
        } catch (Exception e) {
            android.util.Log.e("HistoryFragment", "Error unregistering receiver", e);
        }
    }

    private void loadHistory() {
        SharedPreferences prefs = requireContext().getSharedPreferences("analysis_history", Context.MODE_PRIVATE);
        String historyJson = prefs.getString("history_list", "[]");

        android.util.Log.d("HistoryFragment", "Loading history JSON: " + historyJson);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<AnalysisResult>>(){}.getType();
        List<AnalysisResult> savedHistory = gson.fromJson(historyJson, listType);

        if (savedHistory != null) {
            historyList.clear();
            historyList.addAll(savedHistory);
            adapter.updateData(historyList);
            android.util.Log.d("HistoryFragment", "Loaded " + historyList.size() + " items");
        } else {
            android.util.Log.d("HistoryFragment", "No history found");
        }

        updateButtonStates();
        updateEmptyState();
    }

    private void removeDuplicates() {
        SharedPreferences prefs = requireContext().getSharedPreferences("analysis_history", Context.MODE_PRIVATE);
        String historyJson = prefs.getString("history_list", "[]");

        Gson gson = new Gson();
        Type listType = new TypeToken<List<AnalysisResult>>(){}.getType();
        List<AnalysisResult> historyList = gson.fromJson(historyJson, listType);

        if (historyList != null && historyList.size() > 0) {
            // Supprimer les doublons basés sur la date (à 1 seconde près)
            List<AnalysisResult> uniqueList = new ArrayList<>();
            for (AnalysisResult item : historyList) {
                boolean isDuplicate = false;
                for (AnalysisResult unique : uniqueList) {
                    if (Math.abs(unique.date - item.date) < 1000) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    uniqueList.add(item);
                }
            }

            if (uniqueList.size() != historyList.size()) {
                // Sauvegarder la liste nettoyée
                String cleanedJson = gson.toJson(uniqueList);
                prefs.edit().putString("history_list", cleanedJson).apply();
                android.util.Log.d("HistoryFragment", "Removed " + (historyList.size() - uniqueList.size()) + " duplicates");

                // Recharger l'historique
                loadHistory();
            }
        }
    }

    // Adapter pour le RecyclerView avec gestion de sélection
    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
        private List<AnalysisResult> results;
        private List<Boolean> selectedItems;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        private boolean selectionMode = false;

        public HistoryAdapter(List<AnalysisResult> results) {
            this.results = results;
            this.selectedItems = new ArrayList<>();
            updateSelectedItemsList();
        }

        private void updateSelectedItemsList() {
            selectedItems.clear();
            for (int i = 0; i < results.size(); i++) {
                selectedItems.add(false);
            }
        }

        public void updateData(List<AnalysisResult> newResults) {
            this.results = newResults;
            updateSelectedItemsList();
            notifyDataSetChanged();
        }

        public void setSelectionMode(boolean selectionMode) {
            this.selectionMode = selectionMode;
            if (!selectionMode) {
                clearSelection();
            }
            notifyDataSetChanged();
        }

        public void clearSelection() {
            for (int i = 0; i < selectedItems.size(); i++) {
                selectedItems.set(i, false);
            }
            notifyDataSetChanged();
        }

        public void selectAll(boolean select) {
            for (int i = 0; i < selectedItems.size(); i++) {
                selectedItems.set(i, select);
            }
            notifyDataSetChanged();
        }

        public boolean areAllSelected() {
            if (selectedItems.isEmpty()) return false;
            for (Boolean selected : selectedItems) {
                if (!selected) return false;
            }
            return true;
        }

        public int getSelectedCount() {
            int count = 0;
            for (Boolean selected : selectedItems) {
                if (selected) count++;
            }
            return count;
        }

        public List<AnalysisResult> getSelectedItems() {
            List<AnalysisResult> selected = new ArrayList<>();
            for (int i = 0; i < selectedItems.size() && i < results.size(); i++) {
                if (selectedItems.get(i)) {
                    selected.add(results.get(i));
                }
            }
            return selected;
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
            AnalysisResult result = results.get(position);
            holder.bind(result, position);
        }

        @Override
        public int getItemCount() {
            return results.size();
        }

        class HistoryViewHolder extends RecyclerView.ViewHolder {
            private TextView dateText;
            private TextView durationText;
            private TextView stepCountText;
            private TextView frequencyText;
            private TextView speedText;
            private CheckBox itemCheckbox;
            private View itemContainer;

            HistoryViewHolder(View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.date_text);
                durationText = itemView.findViewById(R.id.duration_text);
                stepCountText = itemView.findViewById(R.id.step_count_text);
                frequencyText = itemView.findViewById(R.id.frequency_text);
                speedText = itemView.findViewById(R.id.speed_text);
                itemCheckbox = itemView.findViewById(R.id.item_checkbox);
                itemContainer = itemView.findViewById(R.id.item_container);
            }

            public void bind(AnalysisResult result, int position) {
                dateText.setText(dateFormat.format(new Date(result.date)));

                int minutes = (int) (result.duration / 60000);
                int seconds = (int) ((result.duration % 60000) / 1000);
                durationText.setText(String.format("Durée: %02d:%02d", minutes, seconds));

                stepCountText.setText(String.format("Pas: %d", result.stepCount));
                frequencyText.setText(String.format("Fréq: %.2f Hz", result.frequency));
                speedText.setText(String.format("Vitesse: %.2f m/s", result.speed));

                // Gestion du mode sélection
                if (selectionMode && position < selectedItems.size()) {
                    itemCheckbox.setVisibility(View.VISIBLE);
                    itemCheckbox.setChecked(selectedItems.get(position));
                } else {
                    itemCheckbox.setVisibility(View.GONE);
                }

                // Click listener pour la sélection
                itemView.setOnClickListener(v -> {
                    if (selectionMode && position < selectedItems.size()) {
                        boolean isSelected = !selectedItems.get(position);
                        selectedItems.set(position, isSelected);
                        itemCheckbox.setChecked(isSelected);
                        updateButtonStates();
                    }
                });

                // Long click listener pour entrer en mode sélection
                itemView.setOnLongClickListener(v -> {
                    if (!selectionMode) {
                        enterSelectionMode();
                        if (position < selectedItems.size()) {
                            selectedItems.set(position, true);
                            notifyDataSetChanged();
                            updateButtonStates();
                        }
                        return true;
                    } else {
                        // En mode sélection, long click pour supprimer directement
                        deleteItem(result);
                        return true;
                    }
                });

                // Checkbox listener
                itemCheckbox.setOnCheckedChangeListener(null); // Clear previous listener
                itemCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (selectionMode && position < selectedItems.size()) {
                        selectedItems.set(position, isChecked);
                        updateButtonStates();
                    }
                });
            }
        }
    }
}