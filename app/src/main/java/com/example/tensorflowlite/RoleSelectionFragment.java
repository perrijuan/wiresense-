package com.example.tensorflowlite;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class RoleSelectionFragment extends Fragment {

    public RoleSelectionFragment() {
        super(R.layout.fragment_role_selection);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botão para Segmentação (Engenheiro)
        View btnEngineer = view.findViewById(R.id.btnEngineer);
        if (btnEngineer != null) {
            btnEngineer.setOnClickListener(v ->
                    Navigation.findNavController(view).navigate(R.id.action_role_to_segmentation));
        }

        // Botão para Analytics (Analista)
        View btnAnalyst = view.findViewById(R.id.btnAnalyst);
        if (btnAnalyst != null) {
            btnAnalyst.setOnClickListener(v ->
                    Navigation.findNavController(view).navigate(R.id.action_role_to_analytics));
        }

        // Botão para Chatbot (Suporte)
        View btnSupport = view.findViewById(R.id.btnSupport);
        if (btnSupport != null) {
            btnSupport.setOnClickListener(v ->
                    Navigation.findNavController(view).navigate(R.id.action_role_to_chatbot));
        }
    }
}