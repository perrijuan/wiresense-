package com.example.tensorflowlite; // <--- Verifique se o pacote é este mesmo!

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class LoginFragment extends Fragment {

    // Construtor que diz ao Android qual layout (XML) usar
    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Vincula o botão "Entrar" à ação de navegar
        // O ID R.id.btnLogin deve existir no seu fragment_login.xml
        View btnLogin = view.findViewById(R.id.btnLogin);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                // Navega para a próxima tela (RoleSelection)
                // Certifique-se que o ID 'action_login_to_role' existe no nav_graph.xml
                Navigation.findNavController(view).navigate(R.id.action_login_to_role);
            });
        }
    }
}