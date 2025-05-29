package com.example.mobigait.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mobigait.R;
import com.example.mobigait.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observer pour le texte du ViewModel
        homeViewModel.getText().observe(getViewLifecycleOwner(), text -> {
            if (binding.textHome != null) {
                binding.textHome.setText(text);
            }
        });

        // Configuration du bouton pour démarrer l'analyse
        if (binding.startAnalysisButton != null) {
            binding.startAnalysisButton.setOnClickListener(v -> {
                // Navigate to Analysis Fragment
                Navigation.findNavController(v).navigate(R.id.nav_analysis);
            });
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}