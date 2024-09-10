package com.example.hc_frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentActivity;

import com.example.hc_frontend.domain.Usuario;

import java.io.Serializable;

public class MenuActivity extends FragmentActivity {

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Recebe o objeto Usuario da MainActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("usuario")) {
            usuario = (Usuario) intent.getSerializableExtra("usuario");
        }

        ImageButton btnProfile = findViewById(R.id.btnProfile);
        ImageButton btnAppointments = findViewById(R.id.btnAppointments);
        ImageButton btnExams = findViewById(R.id.btnExams);
        ImageButton btnMedications = findViewById(R.id.btnMedications);
        ImageButton btnMessages = findViewById(R.id.btnMessages);

//        btnProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navegar para a tela de Perfil
//                Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
//                startActivity(intent);
//            }
//        });

//        btnAppointments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent consultaIntent = new Intent(MenuActivity.this, PacienteActivty.class);
//                consultaIntent.putExtra("usuario", (Serializable) usuario);
//                startActivity(consultaIntent);
//            }
//        });

        btnAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent consultaIntent = new Intent(MenuActivity.this, ConsultaActivity.class);
                consultaIntent.putExtra("usuario", (Serializable) usuario);
                startActivity(consultaIntent);
            }
        });

//        btnExams.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navegar para a tela de Exames
//                Intent intent = new Intent(MenuActivity.this, ExamsActivity.class);
//                startActivity(intent);
//            }
//        });

        btnMedications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para a tela de Medicamentos
                Intent intent = new Intent(MenuActivity.this, TelaMedicamentosActivity.class);
                startActivity(intent);
            }
        });

//        btnMessages.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navegar para a tela de Mensagens
//                Intent intent = new Intent(MenuActivity.this, MessagesActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}